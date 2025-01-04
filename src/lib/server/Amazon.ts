import { AMAZON_SECRET_KEY, AMAZON_ACCESS_KEY } from '$lib/config';
import { exec } from 'child_process';
import { promisify } from 'util';
import { type GetItemsResponse } from '$lib/entities/getitems';

const execAsync = promisify(exec);

if (!AMAZON_ACCESS_KEY) {
	console.error('Missing AMAZON_ACCESS_KEY');
}
if (!AMAZON_SECRET_KEY) {
	console.error('Missing AMAZON_SECRET_KEY');
}

const credentials = {
	accessKey: AMAZON_ACCESS_KEY!,
	secretKey: AMAZON_SECRET_KEY!,
	partnerTag: 'tokuhirom-22',
	marketplace: 'www.amazon.co.jp'
};

// blog3 で使う型
export type AmazonProductDetail = {
	asin: string;
	title: string | undefined | null;
	image_medium_url: string | undefined | null;
	link: string;
};

export async function fetchProductDetails(
	asins: string[]
): Promise<Record<string, AmazonProductDetail>> {
	if (!credentials.accessKey || !credentials.secretKey) {
		throw new Error(`Missing AMAZON_ACCESS_KEY or AMAZON_SECRET_KEY`);
	}
	console.log(credentials);

	if (asins.length > 10) {
		throw new Error('Maximum of 10 ASINs can be processed at once');
	}

	const invalidAsins = asins.filter((asin) => !/^[a-zA-Z0-9]+$/.test(asin));
	if (invalidAsins.length > 0) {
		throw new Error(`Invalid ASINs: ${invalidAsins.join(', ')}`);
	}

	const command = `perl amazon-batch/getitems.pl ${asins.join(' ')}`;
	console.log(`Running command: ${command}`);
	try {
		// 外部コマンドを実行
		const { stdout, stderr } = await execAsync(command);

		// 標準出力を JSON としてパース
		const response: GetItemsResponse = JSON.parse(stdout);

		if (
			!response ||
			!response.ItemsResult ||
			!response.ItemsResult.Items ||
			response.ItemsResult.Items.length === 0
		) {
			throw new Error(`Invalid response from external command: ${stdout}, ${stderr}`);
		}

		// ASINをキーとしたオブジェクトを生成
		const productDetailsMap: Record<string, AmazonProductDetail> = {};
		for (const item of response.ItemsResult.Items) {
			productDetailsMap[item.ASIN] = {
				asin: item.ASIN,
				title: item.ItemInfo?.Title?.DisplayValue,
				image_medium_url: item.Images?.Primary?.Medium?.URL,
				link: item.DetailPageURL
			};
		}

		return productDetailsMap;
	} catch (error) {
		console.error('Failed to fetch product details', error);
		throw new Error('Failed to fetch product details');
	}
}

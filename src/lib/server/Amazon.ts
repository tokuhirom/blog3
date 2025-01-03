import { AMAZON_SECRET_KEY, AMAZON_ACCESS_KEY } from '$lib/config';
import { exec } from 'child_process';
import { promisify } from 'util';

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

export type AmazonProductDetail = {
	asin: string;
	title: string | undefined | null;
	image_medium_url: string | undefined | null;
	link: string;
};

export async function fetchProductDetails(asin: string): Promise<AmazonProductDetail> {
	if (!credentials.accessKey || !credentials.secretKey) {
		throw new Error(`Missing AMAZON_ACCESS_KEY or AMAZON_SECRET_KEY`);
	}
	console.log(credentials);

	if (!/^[a-zA-Z0-9]+$/.test(asin)) {
		throw new Error(`Invalid ASIN: ${asin}`);
	}

	const command = `perl amazon-batch/main.pl ${asin}`;
	try {
		// 外部コマンドを実行
		const { stdout } = await execAsync(command);

		// 標準出力を JSON としてパース
		const response = JSON.parse(stdout);

		if (
			!response ||
			!response.ItemsResult ||
			!response.ItemsResult.Items ||
			response.ItemsResult.Items.length === 0
		) {
			throw new Error('Invalid response from external command');
		}

		const item = response.ItemsResult.Items[0];
		return {
			asin: asin,
			title: item.ItemInfo?.Title?.DisplayValue,
			image_medium_url: item.Images?.Primary?.Medium?.URL,
			link: `https://www.amazon.co.jp/dp/${asin}?tag=${credentials.partnerTag}`
		};
	} catch (error) {
		console.error('Failed to fetch product details', error);
		throw new Error('Failed to fetch product details');
	}
}

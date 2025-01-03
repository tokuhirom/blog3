// import { GetItemsRequest, PartnerType, Host, Region } from 'paapi5-typescript-sdk';
import { AMAZON_SECRET_KEY, AMAZON_ACCESS_KEY } from '$lib/config';

import amazonPaapi from 'amazon-paapi';

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

	/*
	const request = new GetItemsRequest(
		{
			ItemIds: [asin],
			Resources: ['Images.Primary.Medium', 'ItemInfo.Title']
		},
		credentials.partnerTag,
		PartnerType.ASSOCIATES,
		credentials.accessKey,
		credentials.secretKey,
		Host.JAPAN,
		Region.JAPAN
	);
	console.log(Host.JAPAN);
	console.log(Region.JAPAN);
	console.log(request);
	const response = await request.send();
	console.log(response);
	 */

	const item = response.ItemsResult.Items[0];
	console.log(`got item: ${item}`);
	return {
		asin: asin,
		title: item.ItemInfo?.Title?.DisplayValue,
		image_medium_url: item.Images?.Primary?.Medium?.URL,
		link: `https://www.amazon.co.jp/dp/${asin}?tag=${credentials.partnerTag}`
	};
}

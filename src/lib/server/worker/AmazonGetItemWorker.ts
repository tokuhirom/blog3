import { fetchProductDetails } from '../Amazon';
import { getNotProcessedAsins, insertProductDetail } from '../repository/AmazonRepository';

async function doGetItem(): Promise<void> {
	const asins = await getNotProcessedAsins();
	if (asins.length > 0) {
		const asinsToProcess = asins.slice(0, 10);
		const productDetailsMap = await fetchProductDetails(asinsToProcess);
		for (const asin of asinsToProcess) {
			const productDetail = productDetailsMap[asin];
			if (productDetail) {
				await insertProductDetail(productDetail);
			} else {
				console.warn(`No product detail found for ASIN: ${asin}`);
			}
		}
		console.log(`AmazonGetItemWorker: Processed ${JSON.stringify(productDetailsMap)}.`);
	} else {
		console.log('AmazonGetItemWorker: No data to process.');
	}
}

export function startAmazonGetItemWorker() {
	console.log('Starting AmazonGetItemWorker...');
	setTimeout(doGetItem, 1000);
	setInterval(doGetItem, 1000 * 60 * 60);
}

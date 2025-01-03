import type { RequestHandler } from '@sveltejs/kit';
import { getNotProcessedAsins, insertProductDetail } from '$lib/server/repository/AmazonRepository';
import { fetchProductDetails } from '$lib/server/Amazon';

// amazon のデータを一個処理する
export const GET: RequestHandler = async () => {
	try {
		const asins = await getNotProcessedAsins();
		if (asins.length > 0) {
			const fromApi = await fetchProductDetails(asins[0]);
			await insertProductDetail(fromApi);
			return new Response(JSON.stringify(fromApi), { status: 200 });
		} else {
			return new Response(JSON.stringify({ error: 'No data' }), { status: 500 });
		}
	} catch (error) {
		console.error(error);
		return new Response(JSON.stringify({ error: 'Database error' }), { status: 500 });
	}
};

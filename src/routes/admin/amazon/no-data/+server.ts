import type { RequestHandler } from '@sveltejs/kit';
import { getNotProcessedAsins } from '$lib/server/repository/AmazonRepository';

// amazon のデータを取得できたないものをリストアップする
export const GET: RequestHandler = async () => {
	try {
		const asins = await getNotProcessedAsins();
		return new Response(JSON.stringify({ asins }), { status: 200 });
	} catch (error) {
		console.error(error);
		return new Response(JSON.stringify({ error: 'Database error' }), { status: 500 });
	}
};

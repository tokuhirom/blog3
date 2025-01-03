import { type RowDataPacket } from 'mysql2/promise';

import { fetchProductDetails, type AmazonProductDetail } from '$lib/Amazon';
import { db } from '$lib/db';

export async function getProductDetailsWithCache(asin: string): Promise<AmazonProductDetail> {
	const [rows] = await db.query<AmazonProductDetail[] & RowDataPacket[]>(
		`select * from amazon_cache where asin=?`,
		[asin]
	);
	if (rows.length > 0) {
		console.log(`hit amazon cache: ${rows[0]}`);
		return rows[0];
	}

	const fromApi = await fetchProductDetails(asin);
	console.log(`fetched from amazon paapi: ${fromApi}`);
	if (fromApi) {
		await db.query(
			`INSERT INTO amazon_cache (asin, title, image_medium_url, link) VALUES (?, ?, ?, ?)`,
			[fromApi.asin, fromApi.title, fromApi.image_medium_url, fromApi.link]
		);
	}
	return fromApi;
}

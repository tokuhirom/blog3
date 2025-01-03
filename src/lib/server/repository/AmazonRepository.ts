import { type RowDataPacket } from 'mysql2/promise';

import { type AmazonProductDetail } from '$lib/server/Amazon';
import { db } from '$lib/server/db';
import { type Entry } from '$lib/entity';

export async function getProductDetails(asin: string): Promise<AmazonProductDetail | null> {
	const [rows] = await db.query<AmazonProductDetail[] & RowDataPacket[]>(
		`select * from amazon_cache where asin=?`,
		[asin]
	);
	if (rows.length > 0) {
		console.log(`hit amazon cache: ${rows[0]}`);
		return rows[0];
	} else {
		return null;
	}
}

export async function insertProductDetail(fromApi: AmazonProductDetail): Promise<void> {
	await db.query(
		`INSERT INTO amazon_cache (asin, title, image_medium_url, link) VALUES (?, ?, ?, ?)`,
		[fromApi.asin, fromApi.title, fromApi.image_medium_url, fromApi.link]
	);
}

export async function getNotProcessedAsins() {
	const [rows] = await db.query<Entry[] & RowDataPacket[]>(
		`SELECT * FROM entry WHERE body like '%[asin:%' LIMIT 100`,
		[]
	);

	const asinSet = new Set<string>();
	const asinRegex = /\[asin:([A-Za-z0-9]+):detail\]/g;
	for (const row of rows) {
		const body = row.body;
		let match: RegExpExecArray | null;
		while ((match = asinRegex.exec(body)) !== null) {
			asinSet.add(match[1]);
		}
	}

	const asinArray = Array.from(asinSet);
	const placeholders = asinArray.map(() => '?').join(', ');
	const [existingRows] = await db.query<RowDataPacket[]>(
		`SELECT asin FROM amazon_cache WHERE asin IN (${placeholders})`,
		asinArray
	);

	const existingAsins = new Set(existingRows.map((row) => row.asin));
	return asinArray.filter((asin) => !existingAsins.has(asin));
}

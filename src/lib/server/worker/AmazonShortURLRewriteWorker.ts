import type { Entry } from '$lib/entity';
import { db } from '$lib/server/db';
import type { RowDataPacket } from 'mysql2';
import { amazonShortUrlToAsin } from '$lib/server/service/AmazonService';

async function doRewrite(): Promise<void> {
	console.log('get entries to rewrite.');
	const [rows] = await db.query<Entry[] & RowDataPacket[]>(
		`SELECT * FROM entry WHERE body like '%https://amzn.to/%' LIMIT 100`
	);

	console.log(`Found ${rows.length} entries to rewrite.`);
	for (const row of rows) {
		const body = row.body;
		const m = body.match(/https:\/\/amzn.to\/[a-zA-Z0-9]+/);
		if (!m) {
			continue;
		}
		for (const url of m) {
			console.log(`Rewriting ${url}`);
			const asin = await amazonShortUrlToAsin(url);
			const newBody = body.replace(url, `[asin:${asin}:detail]`);

			console.log(`########## Rewriting ${url} to [asin:${asin}:detail]`);
			console.log(newBody);

			await db.query('UPDATE entry SET body = ? WHERE path = ?', [newBody, row.path]);
		}
	}
}

export function startAmazonShortURLRewriteWorker() {
	console.log('Starting AmazonShortURLRewriteWorker...');
	setTimeout(doRewrite, 1000);
	setInterval(doRewrite, 1000 * 60 * 60);
}

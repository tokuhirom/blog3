import type { Entry } from '$lib/entity';
import { db } from '$lib/server/db';
import type { RowDataPacket } from 'mysql2/promise';

export class EntryImageRepository {
	async getNotProcessedEntries(): Promise<Entry[]> {
		const [rows] = await db.query<Entry[] & RowDataPacket[]>(
			`
            SELECT entry.*
            FROM entry
                LEFT JOIN entry_image ON (entry.path = entry_image.path)
            WHERE entry_image.path IS NULL
                AND (body LIKE '%[asin:%' OR body LIKE '%.png%' OR body LIKE '%.jpg%')
            ORDER BY updated_at DESC
            `,
			[]
		);
		return rows;
	}

	async insertEntryImage(path: string, url: string | null): Promise<void> {
		if (path === null) {
			throw new Error('path is required');
		}

		await db.query(
			`
            INSERT INTO entry_image (path, url)
            VALUES (?, ?)
            `,
			[path, url]
		);
	}
}

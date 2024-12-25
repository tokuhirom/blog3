import { type RowDataPacket } from 'mysql2/promise';
import { db, type Entry } from '$lib/db';

export class PublicEntryRepository {
	static async getEntryByTitle(title: string): Promise<Entry | null> {
		try {
			const [rows] = await db.query<Entry[] & RowDataPacket[]>(
				`SELECT * FROM entry
				WHERE title = ?
				    AND status='published'`,
				[title]
			);
			if (rows.length === 0) {
				return null;
			}
			return rows[0];
		} catch (error) {
			console.error(`Error fetching entry: ${error} title=${title}`);
			throw error;
		}
	}

	static async getEntry(path: string): Promise<Entry | null> {
		try {
			const [rows] = await db.query<Entry[] & RowDataPacket[]>(
				`SELECT * FROM entry
				WHERE path = ?
				    AND status='published'`,
				[path]
			);
			if (rows.length === 0) {
				return null;
			}
			return rows[0];
		} catch (error) {
			console.error('Error fetching entry:', error, path);
			throw error;
		}
	}

	/**
	 * Get paginated entries.
	 *
	 * @param page - The page number (1-based).
	 * @param entriesPerPage - Number of entries per page.
	 * @returns A list of entries for the specified page.
	 */
	static async getPaginatedEntry(
		page: number = 1,
		entriesPerPage: number = 100
	): Promise<{
		entries: Entry[];
		hasNext: boolean;
	}> {
		if (page < 1) {
			throw new Error('Page number must be greater than or equal to 1.');
		}

		const offset = (page - 1) * entriesPerPage; // OFFSET の計算

		// クエリ実行
		const [entries] = await db.query<Entry[] & RowDataPacket[]>(
			`
			SELECT * FROM entry
			WHERE status = 'published'
			ORDER BY path DESC
			LIMIT ? OFFSET ?
			`,
			[entriesPerPage + 1, offset]
		);

		// 次ページがあるかどうか
		const hasNext = entries.length > entriesPerPage;
		if (hasNext) {
			entries.pop();
		}

		return {
			entries,
			hasNext
		};
	}
}

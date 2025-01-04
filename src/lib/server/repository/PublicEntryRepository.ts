import { type RowDataPacket } from 'mysql2/promise';
import { db } from '$lib/server/db';
import { type Entry, type EntryImageAware, type HasDestTitle } from '$lib/entity';

export class PublicEntryRepository {
	static async getEntryByTitle(title: string): Promise<Entry | null> {
		try {
			const [rows] = await db.query<Entry[] & RowDataPacket[]>(
				`SELECT * FROM entry
				WHERE title = ?
				    AND visibility='public'`,
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
				    AND visibility='public'`,
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
			WHERE visibility = 'public'
			ORDER BY published_at DESC
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

	/**
	 * Get the links that the entry points to.
	 *
	 * @param srcPath The path of the entry
	 * @returns Object. Key is the title of the destination entry(lower cased). Value is the path of the destination entry.
	 */
	static async getLinkedEntryPaths(srcPath: string): Promise<{ [key: string]: string | null }> {
		const [rows] = await db.query<RowDataPacket[]>(
			`
			SELECT entry_link.dst_title, dest_entry.path dst_path
			FROM entry_link
				LEFT JOIN entry dest_entry ON (dest_entry.title = entry_link.dst_title AND dest_entry.visibility = 'public')
			WHERE entry_link.src_path = ?
				AND dest_entry.visibility = 'public'
			`,
			[srcPath]
		);
		const links: { [key: string]: string | null } = {};
		rows.forEach((row) => {
			links[row.dst_title.toLowerCase()] = row.dst_path;
		});
		return links;
	}

	/**
	 * Get the links that the entry points to.
	 *
	 * @param srcPath The path of the entry
	 * @returns Object. Key is the title of the destination entry(lower cased). Value is the path of the destination entry.
	 */
	async getLinkedEntries(srcPath: string): Promise<(HasDestTitle & Entry & EntryImageAware)[]> {
		const [rows] = await db.query<(HasDestTitle & Entry & EntryImageAware)[] & RowDataPacket[]>(
			`
			SELECT DISTINCT
				entry_link.dst_title AS dst_title,
				dest_entry.title title,
				dest_entry.path path,
				dest_entry.body,
				dest_entry.visibility,
				dest_entry.format,
				dest_entry.created_at,
				dest_entry.updated_at,
				entry_image.url AS image_url
			FROM entry_link
				LEFT JOIN entry dest_entry ON (dest_entry.title = entry_link.dst_title)
				LEFT JOIN entry_image ON (dest_entry.path = entry_image.path)
			WHERE entry_link.src_path = ?
				AND dest_entry.visibility = 'public'
			`,
			[srcPath]
		);
		return rows;
	}

	/**
	 * このタイトルのエントリにリンクしているエントリのリストを取得する。
	 */
	async getEntriesByLinkedTitle(targetTitle: string): Promise<(Entry & EntryImageAware)[]> {
		console.log(`getEntriesByLinkedTitle: ${targetTitle}`);
		const [rows] = await db.query<(Entry & EntryImageAware)[] & RowDataPacket[]>(
			`
			SELECT DISTINCT entry.*
			FROM entry_link
				INNER JOIN entry ON (entry.path = entry_link.src_path)
			WHERE entry_link.dst_title = ?
				AND entry.visibility = 'public'
			`,
			[targetTitle]
		);
		return rows;
	}

	/**
	 * このタイトルのエントリにリンクしているエントリのリストを取得する。
	 */
	async getEntriesByLinkedTitles(
		origPath: string,
		targetTitles: string[]
	): Promise<(HasDestTitle & Entry & EntryImageAware)[]> {
		if (targetTitles.length === 0) {
			return [];
		}
		const placeholders = targetTitles.map(() => 'LOWER(?)').join(', ');
		const [rows] = await db.query<(HasDestTitle & Entry & EntryImageAware)[] & RowDataPacket[]>(
			`
			SELECT DISTINCT dst_title, entry.*, entry_image.url AS image_url
			FROM entry_link
				INNER JOIN entry ON (entry.path = entry_link.src_path)
				LEFT JOIN entry_image ON (entry.path = entry_image.path)
			WHERE entry.path != ? AND LOWER(entry_link.dst_title) IN (${placeholders})
				AND dst_title != LOWER(entry.title)
				AND entry.visibility = 'public'
			`,
			[origPath, ...targetTitles]
		);
		return rows;
	}
}

import mysql, { type ResultSetHeader, type Pool, type RowDataPacket } from 'mysql2/promise';
import * as dotenv from 'dotenv';
import { format } from 'date-fns';

dotenv.config();

// Check the environment variables
const requiredEnvVars = [
	'DATABASE_HOST',
	'DATABASE_PORT',
	'DATABASE_USER',
	'DATABASE_PASSWORD',
	'DATABASE_NAME'
];
requiredEnvVars.forEach((varName) => {
	if (!process.env[varName]) {
		console.error(`Required environment variable '${varName}' is missing`);
		// Exit the application
		process.exit(1);
	}
});
console.log(`Connecting to database at ${process.env.DATABASE_HOST}`);

export const db: Pool = mysql.createPool({
	host: process.env.DATABASE_HOST!,
	port: Number(process.env.DATABASE_PORT),
	user: process.env.DATABASE_USER!,
	password: process.env.DATABASE_PASSWORD!,
	database: process.env.DATABASE_NAME!
});

export type Entry = {
	path: string;
	title: string;
	body: string;
	status: 'draft' | 'published';
	format: 'html' | 'mkdn';
	created_at: Date;
	updated_at: Date | null;
};

export class AdminEntryRepository {
	// XXX Bad naming
	async getAllEntries(): Promise<Entry[]> {
		const [rows] = await db.query<Entry[] & RowDataPacket[]>(
			'SELECT * FROM entry ORDER BY path DESC LIMIT 100',
			[]
		);
		return rows;
	}

	async getEntry(path: string): Promise<Entry | null> {
		const [rows] = await db.query<Entry[] & RowDataPacket[]>('SELECT * FROM entry WHERE path = ?', [
			path
		]);

		return rows.length > 0 ? rows[0] : null;
	}

	/**
	 * Update an entry by path
	 */
	async updateEntry(
		path: string,
		data: { title: string; body: string; status: 'draft' | 'published' }
	): Promise<void> {
		// クエリを実行
		const [result] = await db.query<ResultSetHeader>(
			`
      UPDATE entry
      SET title = ?, body = ?, status = ?
      WHERE path = ?
      `,
			[data.title, data.body, data.status, path]
		);

		// 更新が成功したかをチェック
		if (result.affectedRows === 0) {
			throw new Error('Entry not found or no changes applied');
		}
	}

	/**
	 * Delete an entry by path
	 */
	async deleteEntry(path: string): Promise<void> {
		const [result] = await db.query<ResultSetHeader>('DELETE FROM entry WHERE path = ?', [path]);

		if (result.affectedRows === 0) {
			throw new Error('Entry not found');
		}
	}

	async createEntry(data: {
		title: string;
		body: string;
		status: 'draft' | 'published';
	}): Promise<string> {
		const pathFormatter = 'yyyy/MM/dd/HHmmss';
		const path = format(new Date(), pathFormatter);

		// エントリ作成クエリ
		await db.query(
			`
	  INSERT INTO entry (path, title, body, status)
	  VALUES (?, ?, ?, ?)
	  `,
			[path, data.title, data.body, data.status]
		);

		return path;
	}

	/**
	 * Get entries older than the given path.
	 */
	async getEntriesOlderThan(lastPath: string, limit = 100): Promise<Entry[]> {
		const [rows] = await db.query<Entry[] & RowDataPacket[]>(
			`
			SELECT * FROM entry
			WHERE path < ?
			ORDER BY path DESC
			LIMIT ?
			`,
			[lastPath, limit]
		);
		return rows;
	}
}

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

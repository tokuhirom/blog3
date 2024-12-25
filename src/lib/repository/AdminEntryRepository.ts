import { type ResultSetHeader, type RowDataPacket } from 'mysql2/promise';
import { db, type Entry } from '$lib/db';
import { format } from 'date-fns';

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
		data: {
			title: string;
			body: string;
			visibility: 'private' | 'public';
			updated_at: string | null;
		}
	): Promise<Entry> {
		// updated_at is only for optimistic concurrency control

		// クエリを実行
		const [result] = await db.query<ResultSetHeader>(
			`
			UPDATE entry
			SET title = ?, body = ?, visibility = ?
			WHERE path = ? AND (updated_at = ? OR (updated_at IS NULL AND ? IS NULL))
			`,
			[data.title, data.body, data.visibility, path, data.updated_at, data.updated_at]
		);

		// 更新が成功したかをチェック
		if (result.affectedRows === 0) {
			const [existingEntry] = await db.query<ResultSetHeader[]>(
				`SELECT 1 FROM entry WHERE path = ?`,
				[path]
			);

			if (existingEntry.length === 0) {
				throw new Error('Entry not found');
			} else {
				throw new Error('Update conflict: Reload the entry and try again');
			}
		}

		const entry = await this.getEntry(path);
		if (!entry) {
			// very rare case
			throw new Error('Cannot get entry after update');
		}
		return entry;
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
		visibility: 'private' | 'public';
	}): Promise<string> {
		const pathFormatter = 'yyyy/MM/dd/HHmmss';
		const path = format(new Date(), pathFormatter);

		// エントリ作成クエリ
		await db.query(
			`
	  INSERT INTO entry (path, title, body, visibility)
	  VALUES (?, ?, ?, ?)
	  `,
			[path, data.title, data.body, data.visibility]
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

import { type Connection, type ResultSetHeader, type RowDataPacket } from 'mysql2/promise';
import { db, type Entry } from '$lib/db';
import { format } from 'date-fns';

export class AdminEntryRepository {
	async getLatestEntries(): Promise<Entry[]> {
		const [rows] = await db.query<Entry[] & RowDataPacket[]>(
			'SELECT * FROM entry ORDER BY updated_at DESC, path DESC LIMIT 100',
			[]
		);
		return rows;
	}

	/**
	 * Get entries older than the given path.
	 */
	async getEntriesOlderThan(lastPath: string, limit = 100): Promise<Entry[]> {
		const [rows] = await db.query<Entry[] & RowDataPacket[]>(
			`
			SELECT * FROM entry
			WHERE path < ?
			ORDER BY updated_at DESC, path DESC
			LIMIT ?
			`,
			[lastPath, limit]
		);
		return rows;
	}

	async getEntry(path: string): Promise<Entry | null> {
		const [rows] = await db.query<Entry[] & RowDataPacket[]>('SELECT * FROM entry WHERE path = ?', [
			path
		]);

		return rows.length > 0 ? rows[0] : null;
	}

	async updateEntryVisibility(path: string, visibility: 'private' | 'public'): Promise<void> {
		await db.query('UPDATE entry SET visibility = ? WHERE path = ?', [visibility, path]);
	}

	/**
	 * Update an entry by path
	 */
	async updateEntry(
		path: string,
		data: {
			title: string;
			body: string;
			updated_at: string | null;
		}
	): Promise<Entry> {
		// updated_at is only for optimistic concurrency control

		console.log('Update entry:', path, data);

		const conn = await db.getConnection();
		try {
			conn.beginTransaction();

			// クエリを実行
			const [result] = await conn.query<ResultSetHeader>(
				`
				UPDATE entry
				SET title = ?, body = ?
				WHERE path = ? AND (updated_at = ? OR (updated_at IS NULL AND ? IS NULL))
				`,
				[data.title, data.body, path, data.updated_at, data.updated_at]
			);

			// 更新が成功したかをチェック
			if (result.affectedRows === 0) {
				const [existingEntry] = await conn.query<ResultSetHeader[]>(
					`SELECT 1 FROM entry WHERE path = ?`,
					[path]
				);

				if (existingEntry.length === 0) {
					throw new Error('Entry not found');
				} else {
					throw new Error('Update conflict: Reload the entry and try again');
				}
			}

			this.updateEntryLink(conn, path, data.body);

			console.log('Entry updated:', path);
			conn.commit();
		} catch (error) {
			console.log('Got unknown error:', path, error);
			conn.rollback();
			throw error;
		} finally {
			console.log('Release connection');
			conn.release();
		}

		const entry = await this.getEntry(path);
		if (!entry) {
			// very rare case
			throw new Error('Cannot get entry after update');
		}
		return entry;
	}

	/**
	 * Update the entry_link table.
	 * You should call this in the transaction.
	 */
	private async updateEntryLink(conn: Connection, path: string, body: string): Promise<void> {
		// リンクを記録する
		// entry.body からリンクを抽出する
		// [[Foobar]] のような記法が対象となる。まず、正規表現ですべて抽出する。
		let links: string[] = body.match(/\[\[(.+?)\]\]/g) || [];
		// 小文字に変換して重複を削除する
		links = Array.from(new Set(links.map((link) => link.toLowerCase())));
		// 一旦現在のものを削除する
		await conn.query(
			`
			DELETE FROM entry_link WHERE src_path = ?
			`,
			[path]
		);

		// entry_link テーブルに保存する
		if (links.length > 0) {
			const values = links.map((link) => [path, link.slice(2, -2)]);
			const placeholders = values.map(() => '(?, ?)').join(', ');
			await conn.query(
				`
				INSERT INTO entry_link (src_path, dst_title)
				VALUES ${placeholders}
				`,
				values.flat()
			);
		}
	}

	async updateEntryLinkAll(): Promise<void> {
		// Find the entries that have links
		const [entries] = await db.query<RowDataPacket[]>(
			`
			SELECT path, body
			FROM entry
			WHERE body LIKE '%[[%'
			`
		);

		// Update the entry_link table for each entry
		for (const entry of entries) {
			const path = entry.path;
			const body = entry.body;

			const conn = await db.getConnection();
			try {
				conn.beginTransaction();
				await this.updateEntryLink(conn, path, body);
				conn.commit();
			} catch (error) {
				console.error('Error while updating entry_link:', path, error);
				conn.rollback();
			} finally {
				conn.release();
			}
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

	private static genDefaultTitle(): string {
		const date = new Date();
		const title = format(date, 'yyyyMMddHHmmss');
		return title;
	}

	async createEmptyEntry(title: string = AdminEntryRepository.genDefaultTitle()): Promise<string> {
		const date = new Date();
		const path = format(date, 'yyyy/MM/dd/HHmmss');

		await db.query(
			`
            INSERT INTO entry (path, title, body, visibility)
            VALUES (?, ?, ?, ?)
            `,
			[path, title, '', 'private']
		);

		return path;
	}

	/**
	 * Get the links that the entry points to.
	 *
	 * @param srcPath The path of the entry
	 * @returns Object. Key is the title of the destination entry(lower cased). Value is the path of the destination entry.
	 */
	async getLinksBySrcPath(srcPath: string): Promise<{ [key: string]: string | null }> {
		const [rows] = await db.query<RowDataPacket[]>(
			`
			SELECT entry_link.dst_title, dest_entry.path dst_path
			FROM entry_link
				LEFT JOIN entry dest_entry ON (dest_entry.title = entry_link.dst_title)
			WHERE entry_link.src_path = ?
			`,
			[srcPath]
		);
		const links: { [key: string]: string | null } = {};
		rows.forEach((row) => {
			links[row.dst_title.toLowerCase()] = row.dst_path;
		});
		return links;
	}
}

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

		console.log('Update entry:', path, data);

		const conn = await db.getConnection();
		try {
			conn.beginTransaction();

			// クエリを実行
			const [result] = await conn.query<ResultSetHeader>(
				`
				UPDATE entry
				SET title = ?, body = ?, visibility = ?
				WHERE path = ? AND (updated_at = ? OR (updated_at IS NULL AND ? IS NULL))
				`,
				[data.title, data.body, data.visibility, path, data.updated_at, data.updated_at]
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

			// リンクを記録する
			// entry.body からリンクを抽出する
			// [[Foobar]] のような記法が対象となる。まず、正規表現ですべて抽出する。
			const links = data.body.match(/\[\[(.+?)\]\]/g) || [];
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
	 * Delete an entry by path
	 */
	async deleteEntry(path: string): Promise<void> {
		const [result] = await db.query<ResultSetHeader>('DELETE FROM entry WHERE path = ?', [path]);

		if (result.affectedRows === 0) {
			throw new Error('Entry not found');
		}
	}

	static genDefaultTitle(): string {
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

	/**
	 * Get the links that the entry points to.
	 *
	 * @param srcPath The path of the entry
	 * @returns Object. Key is the title of the destination entry. Value is the path of the destination entry.
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
			links[row.dst_title] = row.dst_path;
		});
		return links;
	}
}

import { type Connection, type ResultSetHeader, type RowDataPacket } from 'mysql2/promise';
import { db, type Entry } from '$lib/db';
import { format } from 'date-fns';
import { extractLinks } from '$lib/server/markdown';

export class AdminEntryRepository {
	async getLatestEntries(): Promise<Entry[]> {
		const [rows] = await db.query<Entry[] & RowDataPacket[]>(
			'SELECT * FROM entry ORDER BY COALESCE(updated_at, created_at) DESC, path DESC LIMIT 100',
			[]
		);
		return rows;
	}

	/**
	 * Get entries older than the given path.
	 */
	async getEntriesOlderThan(last_updated_at: string, limit: number): Promise<Entry[]> {
		const [rows] = await db.query<Entry[] & RowDataPacket[]>(
			`
			SELECT * FROM entry
			WHERE updated_at <= ?
			ORDER BY updated_at DESC, path DESC
			LIMIT ?
			`,
			[last_updated_at, limit]
		);
		return rows;
	}

	async getEntry(path: string): Promise<Entry | null> {
		const [rows] = await db.query<Entry[] & RowDataPacket[]>('SELECT * FROM entry WHERE path = ?', [
			path
		]);

		return rows.length > 0 ? rows[0] : null;
	}

	async updateEntryVisibility(path: string, newVisibility: 'private' | 'public'): Promise<void> {
		const connection = await db.getConnection();

		try {
			await connection.beginTransaction();

			// Get the current visibility and published_at
			const [rows] = await connection.query<RowDataPacket[]>(
				'SELECT visibility, published_at FROM entry WHERE path = ?',
				[path]
			);

			if (rows.length === 0) {
				throw new Error('Entry not found');
			}

			const { visibility, published_at } = rows[0];

			// Update visibility
			await connection.query('UPDATE entry SET visibility = ? WHERE path = ?', [
				newVisibility,
				path
			]);

			// If visibility changed from private to public and published_at is null, set published_at to current time
			if (visibility === 'private' && newVisibility === 'public' && !published_at) {
				await connection.query(
					'UPDATE entry SET published_at = NOW() WHERE path = ? AND published_at IS NULL',
					[path]
				);
			}

			await connection.commit();
		} catch (error) {
			await connection.rollback();
			throw error;
		} finally {
			connection.release();
		}
	}

	/**
	 * Update an entry title by path
	 */
	async updateEntryTitle(
		path: string,
		data: {
			title: string;
		}
	): Promise<Entry> {
		console.log('Update entry title:', path, data);
		const [result] = await db.query<ResultSetHeader>(
			`
			UPDATE entry
			SET title = ?
			WHERE path = ?
			`,
			[data.title, path]
		);
		if (result.affectedRows === 0) {
			throw new Error('Entry not found');
		}

		const entry = await this.getEntry(path);
		if (!entry) {
			// very rare case
			throw new Error('Cannot get entry after update');
		}
		return entry;
	}

	/**
	 * Update an entry by path
	 */
	async updateEntryBody(
		path: string,
		data: {
			body: string;
		}
	): Promise<Entry> {
		console.log('Update entry body:', path, data);

		const conn = await db.getConnection();
		try {
			conn.beginTransaction();

			// クエリを実行
			const [result] = await conn.query<ResultSetHeader>(
				`
				UPDATE entry
				SET body = ?
				WHERE path = ?
				`,
				[data.body, path]
			);

			// 更新が成功したかをチェック
			if (result.affectedRows === 0) {
				throw new Error('Entry not found');
			}

			const entry = await this.getEntry(path);
			if (!entry) {
				// very rare case
				throw new Error('Cannot get entry after update');
			}

			this.updateEntryLink(conn, path, entry.title, data.body);

			console.log('Entry updated:', path);
			conn.commit();

			return entry;
		} catch (error) {
			console.log('Got unknown error:', path, error);
			conn.rollback();
			throw error;
		} finally {
			console.log('Release connection');
			conn.release();
		}
	}

	/**
	 * Update the entry_link table.
	 * You should call this in the transaction.
	 */
	private async updateEntryLink(
		conn: Connection,
		path: string,
		title: string,
		body: string
	): Promise<void> {
		// リンクを記録する
		// entry.body からリンクを抽出する
		const links = extractLinks(body).filter((link) => link.toLowerCase() !== title.toLowerCase());
		// 一旦現在のものを削除する
		await conn.query(
			`
			DELETE FROM entry_link WHERE src_path = ?
			`,
			[path]
		);

		// entry_link テーブルに保存する
		if (links.length > 0) {
			const values = links.map((link) => [path, link]);
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
			SELECT path, title, body
			FROM entry
			WHERE body LIKE '%[[%'
			`
		);

		// Update the entry_link table for each entry
		for (const entry of entries) {
			const path = entry.path;
			const title = entry.title;
			const body = entry.body;

			const conn = await db.getConnection();
			try {
				conn.beginTransaction();
				await this.updateEntryLink(conn, path, title, body);
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
	private async getLinksBySrcPath2(srcPath: string): Promise<(HasDestTitle & Entry)[]> {
		const [rows] = await db.query<(HasDestTitle & Entry)[] & RowDataPacket[]>(
			`
			SELECT DISTINCT entry_link.dst_title AS dst_title, dest_entry.title title, dest_entry.path path, dest_entry.body, dest_entry.visibility, dest_entry.format, dest_entry.created_at, dest_entry.updated_at
			FROM entry_link
				LEFT JOIN entry dest_entry ON (dest_entry.title = entry_link.dst_title)
			WHERE entry_link.src_path = ?
			`,
			[srcPath]
		);
		return rows;
	}

	/**
	 * Get the links that the entry points to.
	 *
	 * @param srcPath The path of the entry
	 * @returns Object. Key is the title of the destination entry(lower cased). Value is the path of the destination entry.
	 */
	async getLinksBySrcPath(srcPath: string): Promise<{ [key: string]: string | null }> {
		const links: { [key: string]: string | null } = {};
		(await this.getLinksBySrcPath2(srcPath)).forEach((row) => {
			links[row.dst_title.toLowerCase()] = row.path;
		});
		return links;
	}

	/**
	 * Get two hop links from the entry.
	 */
	async getTwoHopLinksBySrcPath(targetPath: string, targetTitle: string): Promise<LinkPalletData> {
		if (!targetTitle) {
			throw new Error('Missing targetTitle');
		}

		// このエントリがリンクしているページのリストを取得
		const links = await this.getLinksBySrcPath2(targetPath);
		console.log(
			'links:',
			links.map((link) => link.dst_title)
		);
		// このエントリにリンクしているページのリストを取得
		const reverseLinks = await this.getEntriesByLinkedTitle(targetTitle);
		console.log(
			'reverseLinks:',
			reverseLinks.map((link) => link.title)
		);
		// links の指す先のタイトルにリンクしているエントリのリストを取得
		const twohopEntries = await this.getEntriesByLinkedTitles(
			targetPath,
			links.map((link) => link.dst_title.toLowerCase())
		);
		console.log(
			'twohopEntries:',
			twohopEntries.map((link) => 'dest=' + link.dst_title + ' ' + link.title)
		);
		// twohopEntries を dst_title でグループ化
		const twohopEntriesByTitle: { [key: string]: Entry[] } = {};
		for (const entry of twohopEntries) {
			if (!twohopEntriesByTitle[entry.dst_title.toLowerCase()]) {
				twohopEntriesByTitle[entry.dst_title.toLowerCase()] = [];
			}
			twohopEntriesByTitle[entry.dst_title.toLowerCase()].push(entry);
		}

		const resultLinks: Entry[] = [];
		const resultTwoHops: TwoHopLink[] = [];
		const newLinks: string[] = [];
		const seenPath = new Set([targetPath]);

		// twohopEntries に入っているエントリのリストを作成
		for (const link of links) {
			if (link.path) {
				seenPath.add(link.path);
			}

			if (twohopEntriesByTitle[link.dst_title.toLowerCase()]) {
				resultTwoHops.push({
					src: link,
					links: twohopEntriesByTitle[link.dst_title.toLowerCase()]
				});
				for (const entry of twohopEntriesByTitle[link.dst_title.toLowerCase()]) {
					seenPath.add(entry.path);
				}
			} else {
				if (link.body) {
					resultLinks.push(link);
				} else {
					newLinks.push(link.dst_title);
				}
			}
		}
		for (const reverseLink of reverseLinks) {
			if (!seenPath.has(reverseLink.path)) {
				resultLinks.push(reverseLink);
			}
		}

		return {
			newLinks: Array.from(new Set(newLinks)),
			links: Array.from(new Set(resultLinks)),
			twohops: resultTwoHops
		};
	}

	/**
	 * このタイトルのエントリにリンクしているエントリのリストを取得する。
	 */
	private async getEntriesByLinkedTitle(targetTitle: string): Promise<Entry[]> {
		console.log(`getEntriesByLinkedTitle: ${targetTitle}`);
		const [rows] = await db.query<Entry[] & RowDataPacket[]>(
			`
			SELECT DISTINCT entry.*
			FROM entry_link
				INNER JOIN entry ON (entry.path = entry_link.src_path)
			WHERE entry_link.dst_title = ?
			`,
			[targetTitle]
		);
		return rows;
	}

	/**
	 * このタイトルのエントリにリンクしているエントリのリストを取得する。
	 */
	private async getEntriesByLinkedTitles(
		origPath: string,
		targetTitles: string[]
	): Promise<(HasDestTitle & Entry)[]> {
		if (targetTitles.length === 0) {
			return [];
		}
		const placeholders = targetTitles.map(() => 'LOWER(?)').join(', ');
		const [rows] = await db.query<(HasDestTitle & Entry)[] & RowDataPacket[]>(
			`
			SELECT DISTINCT dst_title, entry.*
			FROM entry_link
				INNER JOIN entry ON (entry.path = entry_link.src_path)
			WHERE entry.path != ? AND LOWER(entry_link.dst_title) IN (${placeholders})
				AND dst_title != LOWER(entry.title)
			`,
			[origPath, ...targetTitles]
		);
		return rows;
	}

	async getTitles(): Promise<string[]> {
		const [rows] = await db.query<RowDataPacket[]>(
			`
			SELECT title
			FROM entry
			`
		);
		return rows.map((it) => it.title);
	}
}

export type HasDestTitle = {
	dst_title: string;
};

export type TwoHopLink = {
	src: Entry & HasDestTitle;
	links: Entry[];
};

// 名前が良くない
export type LinkPalletData = {
	newLinks: string[];
	links: Entry[];
	twohops: TwoHopLink[];
};

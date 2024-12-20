import mysql, { type Pool, type RowDataPacket } from 'mysql2/promise';
import * as dotenv from 'dotenv';
import { EntryCache } from './cache';
import { format } from 'date-fns';

dotenv.config();

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

export class EntryModel {
	static async getAllEntries(): Promise<Entry[]> {
		const [rows] = await db.query<Entry[] & RowDataPacket[]>(
			'SELECT * FROM entry ORDER BY path DESC LIMIT 100',
			[]
		);
		return rows;
	}

	static async getEntry(path: string): Promise<Entry | null> {
		const [rows] = await db.query<Entry[] & RowDataPacket[]>('SELECT * FROM entry WHERE path = ?', [
			path
		]);

		return rows.length > 0 ? rows[0] : null;
	}

	/**
	 * Update an entry by path
	 */
	static async updateEntry(
		path: string,
		data: { title: string; body: string; status: 'draft' | 'published' }
	): Promise<void> {
		// クエリを実行
		const [result] = await db.query(
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

		EntryCache.clear();
	}

	/**
	 * Delete an entry by path
	 */
	static async deleteEntry(path: string): Promise<void> {
		const [result] = await db.query('DELETE FROM entry WHERE path = ?', [path]);

		if (result.affectedRows === 0) {
			throw new Error('Entry not found');
		}

		EntryCache.clear();
	}

	static async createEntry(data: {
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

		EntryCache.clear();

		return path;
	}
}

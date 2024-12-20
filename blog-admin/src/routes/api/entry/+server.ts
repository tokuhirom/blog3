import { db } from '$lib/db';
import type { RequestHandler } from '@sveltejs/kit';
import { format } from 'date-fns';

export const GET: RequestHandler = async ({ url }) => {
	try {
		// データ取得
		const [rows] = await db.query(
			`
      SELECT path, title, status, format, created_at, updated_at
      FROM entry
      ORDER BY created_at DESC
      `,
			[]
		);

		// ページング情報を含めたレスポンスを返す
		return new Response(
			JSON.stringify({
				entries: rows
			}),
			{ headers: { 'Content-Type': 'application/json' } }
		);
	} catch (error) {
		console.error(error);
		return new Response(JSON.stringify({ error: `Failed to fetch entries: ${error}` }), {
			status: 500,
			headers: { 'Content-Type': 'application/json' }
		});
	}
};

export const POST: RequestHandler = async ({ request }) => {
	try {
		const { title, body, status } = await request.json();

		// 入力値のバリデーション
		if (!title || !body || !status) {
			return new Response(JSON.stringify({ error: 'Invalid input' }), { status: 400 });
		}

		const pathFormatter = 'yyyy/MM/dd/HHmmss';
		const path = format(new Date(), pathFormatter);

		// エントリ作成クエリ
		await db.query(
			`
      INSERT INTO entry (path, title, body, status)
      VALUES (?, ?, ?, ?)
      `,
			[path, title, body, status]
		);

		return new Response(
			JSON.stringify({
				message: 'Entry created successfully',
				entry: { path, title, body, status }
			}),
			{ status: 201, headers: { 'Content-Type': 'application/json' } }
		);
	} catch (error) {
		console.error(error);
		return new Response(JSON.stringify({ error: 'Failed to create entry' }), {
			status: 500,
			headers: { 'Content-Type': 'application/json' }
		});
	}
};

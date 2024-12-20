import { db } from '$lib/db';
import type { RequestHandler } from '@sveltejs/kit';

export const PATCH: RequestHandler = async ({ params, request }) => {
	const path = params.path;

	try {
		// リクエストボディを取得
		const { title, body, status } = await request.json();

		// 必須フィールドのチェック
		if (!title || !body || !status) {
			return new Response(JSON.stringify({ error: 'Missing required fields' }), { status: 400 });
		}

		console.log(`Updating entry with path: '${path}'`);

		// 更新クエリの実行
		const [result] = await db.query(
			`
      UPDATE entry
      SET title = ?, body = ?, status = ?
      WHERE path = ?
      `,
			[title, body, status, path]
		);

		// 更新が成功したかを確認
		if (result.affectedRows === 0) {
			return new Response(JSON.stringify({ error: 'Entry not found' }), { status: 404 });
		}

		return new Response(JSON.stringify({ message: 'Entry updated successfully' }), {
			status: 200,
			headers: { 'Content-Type': 'application/json' }
		});
	} catch (error) {
		console.error(error);
		return new Response(JSON.stringify({ error: 'Failed to update entry' }), {
			status: 500,
			headers: { 'Content-Type': 'application/json' }
		});
	}
};

export const DELETE: RequestHandler = async ({ params }) => {
	const { path } = params;

	if (!path) {
		return new Response(JSON.stringify({ error: 'Path parameter is required' }), {
			status: 400,
			headers: { 'Content-Type': 'application/json' }
		});
	}

	try {
		console.log(`Deleting entry with path: '${path}'`);

		// データベースからエントリを削除
		const [result] = await db.query('DELETE FROM entry WHERE path = ?', [path]);

		if (result.affectedRows === 0) {
			return new Response(JSON.stringify({ error: 'Entry not found' }), {
				status: 404,
				headers: { 'Content-Type': 'application/json' }
			});
		}

		return new Response(JSON.stringify({ message: 'Entry deleted successfully', path }), {
			status: 200,
			headers: { 'Content-Type': 'application/json' }
		});
	} catch (error) {
		console.error(error);
		return new Response(JSON.stringify({ error: 'Failed to delete entry' }), {
			status: 500,
			headers: { 'Content-Type': 'application/json' }
		});
	}
};

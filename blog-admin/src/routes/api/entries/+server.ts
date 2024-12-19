import { db } from '$lib/db';
import type { RequestHandler } from '@sveltejs/kit';

export const GET: RequestHandler = async ({ url }) => {
    // クエリパラメータの取得
    const page = parseInt(url.searchParams.get('page') || '1', 10);
    const entriesPerPage = parseInt(url.searchParams.get('entries') || '30', 10);

    // ページングに基づくデータ取得
    const offset = (page - 1) * entriesPerPage;

    try {
        // データ取得
        const [rows] = await db.query(
            `
      SELECT path, title, status, format, created_at, updated_at
      FROM entry
      ORDER BY created_at DESC
      LIMIT ? OFFSET ?
      `,
            [entriesPerPage, offset]
        );

        // 総エントリ数を取得（ページ総数計算のため）
        const [totalRows] = await db.query('SELECT COUNT(*) AS total FROM entry');
        const totalEntries = totalRows[0]?.total || 0;

        // ページング情報を含めたレスポンスを返す
        return new Response(
            JSON.stringify({
                entries: rows,
                pagination: {
                    currentPage: page,
                    entriesPerPage,
                    totalPages: Math.ceil(totalEntries / entriesPerPage),
                    totalEntries,
                },
            }),
            { headers: { 'Content-Type': 'application/json' } }
        );
    } catch (error) {
        console.error(error);
        return new Response(JSON.stringify({ error: `Failed to fetch entries: ${error}` }), {
            status: 500,
            headers: { 'Content-Type': 'application/json' },
        });
    }
};

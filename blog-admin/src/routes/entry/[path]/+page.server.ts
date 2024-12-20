import { error } from '@sveltejs/kit';
import type { PageServerLoad } from './$types';
import { db } from '$lib/db';

export const load: PageServerLoad = async ({ params }) => {
    console.log("Loading entry page content!");

    const path = params.path;

    const [rows] = await db.query(
        `
        SELECT path, title, body, status, format, created_at, updated_at
        FROM entry
        WHERE path = ?
        `,
        [path]
    );
    if (rows.length === 0) {
        error(404, 'Entry not found: ' + path);
    }
    const entry = rows[0];
    console.log("Entry loaded: ", entry);
    return {
        entry: entry
    }
};

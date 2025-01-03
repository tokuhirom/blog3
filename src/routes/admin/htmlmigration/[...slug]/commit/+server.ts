import type { RequestHandler } from '@sveltejs/kit';
import html2md from 'html-to-md';

export const POST: RequestHandler = async ({ locals, params }) => {
	const path = params.slug;
	if (!path) {
		return new Response(JSON.stringify({ error: 'Missing path' }), { status: 400 });
	}

	try {
		const entry = await locals.adminEntryRepository.getEntry(path);
		const body = html2md(entry.body);
		console.log(`original=${entry.body} mkdn=${body}`);
		await locals.adminEntryRepository.convertToMarkdown(path, body);

		return new Response(JSON.stringify(entry), { status: 200 });
	} catch (error) {
		console.error(error);
		return new Response(JSON.stringify({ error: 'Database error' }), { status: 500 });
	}
};

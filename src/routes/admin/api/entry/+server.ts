import type { RequestHandler } from '@sveltejs/kit';

export const GET: RequestHandler = async ({ url, locals }) => {
	const lastPath = url.searchParams.get('last_path');
	if (!lastPath) {
		return new Response(JSON.stringify({ error: 'Missing last_path' }), { status: 400 });
	}

	const limit = 100;
	try {
		const rows = await locals.adminEntryRepository.getEntriesOlderThan(lastPath, limit);
		return new Response(JSON.stringify(rows), { status: 200 });
	} catch (error) {
		console.error(error);
		return new Response(JSON.stringify({ error: 'Database error' }), { status: 500 });
	}
};

// Create new entry
export const POST: RequestHandler = async ({ locals }) => {
	try {
		const path = await locals.adminEntryRepository.createEmptyEntry();
		return new Response(
			JSON.stringify({
				path
			}),
			{ status: 200 }
		);
	} catch (error) {
		console.error(error);
		return new Response(JSON.stringify({ error: 'Database error' }), { status: 500 });
	}
};

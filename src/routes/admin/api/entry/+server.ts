import type { RequestHandler } from '@sveltejs/kit';

export const GET: RequestHandler = async ({ url, locals }) => {
	const last_updated_at = url.searchParams.get('last_updated_at');
	if (!last_updated_at) {
		return new Response(JSON.stringify({ error: 'Missing last_updated_at' }), { status: 400 });
	}

	const limit = 300;
	try {
		const rows = await locals.adminEntryRepository.getEntriesOlderThan(last_updated_at, limit);
		return new Response(JSON.stringify(rows), { status: 200 });
	} catch (error) {
		console.error(error);
		return new Response(JSON.stringify({ error: 'Database error' }), { status: 500 });
	}
};

// Create new entry
export const POST: RequestHandler = async ({ locals, request }) => {
	try {
		const data = await request.json();
		const path = await locals.adminEntryRepository.createEmptyEntry(data.title);
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

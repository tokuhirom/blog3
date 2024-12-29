import type { RequestHandler } from '@sveltejs/kit';

export const GET: RequestHandler = async ({ locals, params }) => {
	const path = params.slug;
	if (!path) {
		return new Response(JSON.stringify({ error: 'Missing path' }), { status: 400 });
	}

	try {
		const entry = await locals.adminEntryRepository.getEntry(path);
		if (entry) {
			return new Response(JSON.stringify(entry), { status: 200 });
		} else {
			return new Response(JSON.stringify({ error: 'Entry not found' }), { status: 404 });
		}
	} catch (error) {
		console.error(error);
		return new Response(JSON.stringify({ error: 'Database error' }), { status: 500 });
	}
};

export const DELETE: RequestHandler = async ({ locals, params }) => {
	const path = params.slug;
	if (!path) {
		return new Response(JSON.stringify({ error: 'Missing path' }), { status: 400 });
	}

	try {
		await locals.adminEntryRepository.deleteEntry(path);
		return new Response(
			JSON.stringify({
				success: true
			}),
			{ status: 200 }
		);
	} catch (error) {
		console.error(error);
		return new Response(JSON.stringify({ error: 'Database error' }), { status: 500 });
	}
};

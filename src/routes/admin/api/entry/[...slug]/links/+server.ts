import type { RequestHandler } from '@sveltejs/kit';

export const GET: RequestHandler = async ({ locals, params }) => {
	const path = params.slug;
	if (!path) {
		return new Response(JSON.stringify({ error: 'Missing path' }), { status: 400 });
	}

	try {
		const twohops = await locals.adminEntryRepository.getTwoHopLinksBySrcPath(path);
		return new Response(JSON.stringify(twohops), { status: 200 });
	} catch (error) {
		console.error(error);

		const errorMessage = error instanceof Error ? error.message : 'Unknown database error';
		return new Response(JSON.stringify({ error: errorMessage }), { status: 500 });
	}
};
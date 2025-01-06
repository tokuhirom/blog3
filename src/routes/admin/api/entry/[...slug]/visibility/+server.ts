import type { RequestHandler } from '@sveltejs/kit';

export const POST: RequestHandler = async ({ locals, params, request }) => {
	const path = params.slug;
	if (!path) {
		return new Response(JSON.stringify({ error: 'Missing path' }), { status: 400 });
	}

	try {
		const req = await request.json();

		const visibility = req.visibility;

		await locals.adminEntryService.updateEntryVisibility(path, visibility);
		return new Response(JSON.stringify({ visibility }), { status: 200 });
	} catch (error) {
		console.error(error);

		const errorMessage = error instanceof Error ? error.message : 'Unknown database error';
		return new Response(JSON.stringify({ error: errorMessage }), { status: 500 });
	}
};

import type { RequestHandler } from '@sveltejs/kit';

export const POST: RequestHandler = async ({ locals, params, request }) => {
	const path = params.slug;
	if (!path) {
		return new Response(JSON.stringify({ error: 'Missing path' }), { status: 400 });
	}

	try {
		// get title, body, status from request.
		// request is a application/json format.
		// request.json() returns a promise.
		// await waits for the promise to resolve.
		const req = await request.json();

		const title = req.title;
		const body = req.body;
		const status = req.status;
		const updated_at = req.updated_at;

		const entry = await locals.adminEntryRepository.updateEntry(path, {
			title,
			body,
			status,
			updated_at
		});
		return new Response(JSON.stringify(entry), { status: 200 });
	} catch (error) {
		console.error(error);

		const errorMessage = error instanceof Error ? error.message : 'Unknown database error';
		return new Response(JSON.stringify({ error: errorMessage }), { status: 500 });
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

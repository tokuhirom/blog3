import type { RequestHandler } from '@sveltejs/kit';
import { PublicEntryRepository } from '$lib/repository/PublicEntryRepository';

export const GET: RequestHandler = async ({ params }) => {
	const title = params.title;
	if (!title) {
		return new Response(JSON.stringify({ error: 'Missing title' }), { status: 400 });
	}

	try {
		const entry = await PublicEntryRepository.getEntryByTitle(title);
		if (entry === null) {
			return new Response(JSON.stringify({ error: 'Entry not found' }), { status: 404 });
		} else {
			return new Response(JSON.stringify(entry), { status: 200 });
		}
	} catch (error) {
		console.error(error);
		return new Response(JSON.stringify({ error: 'Database error' }), { status: 500 });
	}
};

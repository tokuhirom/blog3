import { error } from '@sveltejs/kit';
import type { PageServerLoad } from './$types';
import { PublicEntryRepository } from '$lib/repository/PublicEntryRepository';
import { renderHTMLByEntry } from '$lib/markdown';

export const load: PageServerLoad = async (params) => {
	console.log('Loading entry page content!(detail)');
	const path = params.params.slug;

	const entry = await PublicEntryRepository.getEntry(path);

	if (entry === null) {
		error(404, {
			message: 'Not found'
		});
	}

	return {
		entry,
		body: renderHTMLByEntry(entry)
	};
};

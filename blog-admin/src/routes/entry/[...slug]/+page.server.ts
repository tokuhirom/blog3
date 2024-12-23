import type { PageServerLoad } from './$types';
import { PublicEntryRepository } from '$lib/db';
import { renderHTMLByEntry } from '$lib/markdown';

export const load: PageServerLoad = async (params) => {
	console.log('Loading entry page content!(detail)');
	const path = params.params.slug;

	const entry = await PublicEntryRepository.getEntry(path);

	return {
		entry,
		body: renderHTMLByEntry(entry)
	};
};

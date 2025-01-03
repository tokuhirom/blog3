import { error } from '@sveltejs/kit';
import type { PageServerLoad } from './$types';
import { PublicEntryRepository } from '$lib/server/repository/PublicEntryRepository';
import { renderHTMLByEntry } from '$lib/markdown';

export const load: PageServerLoad = async (params) => {
	console.log('Loading entry page content!(detail)');
	const path = params.params.slug;

	const entry = await PublicEntryRepository.getEntry(path);
	const links = await PublicEntryRepository.getLinksBySrcPath(path);

	if (entry === null) {
		error(404, {
			message: 'Not found'
		});
	}

	return {
		entry,
		body: renderHTMLByEntry(entry, links)
	};
};

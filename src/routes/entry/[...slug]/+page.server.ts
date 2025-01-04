import { error } from '@sveltejs/kit';
import type { PageServerLoad } from './$types';
import { PublicEntryRepository } from '$lib/server/repository/PublicEntryRepository';
import { renderHTMLByEntry } from '$lib/server/markdown';
import { PublicEntryService } from '$lib/server/service/PublicEntryService';

export const load: PageServerLoad = async (params) => {
	console.log('Loading entry page content!(detail)');
	const path = params.params.slug;

	const publicEntryRepository = new PublicEntryRepository();
	const publicEntryService = new PublicEntryService(publicEntryRepository);

	const entry = await PublicEntryRepository.getEntry(path);
	const links = await PublicEntryRepository.getLinkedEntryPaths(path);

	if (entry === null) {
		error(404, {
			message: 'Not found'
		});
	}

	const body = await renderHTMLByEntry(entry, links);
	const linkPallet = await publicEntryService.getLinkPalletData(path, entry.title);

	return {
		entry,
		body,
		linkPallet
	};
};

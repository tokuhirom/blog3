import type { PageServerLoad } from './$types';

export const load: PageServerLoad = async ({ params, locals }) => {
	const path = params.slug;

	const entry = await locals.adminEntryRepository.getEntry(path);
	if (!entry) {
		return {
			status: 404,
			error: new Error('Entry not found')
		};
	}

	const links = await locals.adminEntryRepository.getLinkedEntryPaths(path);
	const twohops = await locals.adminEntryService.getLinkPalletData(path, entry.title);
	console.log('links:', links);
	return {
		entry,
		links,
		twohops
	};
};

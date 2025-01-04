import type { PageServerLoad } from './$types';

export const load: PageServerLoad = async ({ params, locals }) => {
	const path = params.slug;

	const entry = await locals.adminEntryRepository.getEntry(path);
	const links = await locals.adminEntryRepository.getLinkedEntryPaths(path);
	const twohops = await locals.adminEntryRepository.getLinkPalletData(path, entry.title);
	console.log('links:', links);
	return {
		entry,
		links,
		twohops
	};
};

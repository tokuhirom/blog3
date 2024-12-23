import type { PageServerLoad } from './$types';

export const load: PageServerLoad = async ({ params, locals }) => {
	const path = params.path;

	const entry = await locals.adminEntryRepository.getEntry(path);
	return {
		entry: entry
	};
};

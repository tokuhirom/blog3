import type { PageServerLoad } from './$types';

export const load: PageServerLoad = async ({locals}) => {
	console.log('Loading entry page content!');

	const entries = await locals.adminEntryRepository.getAllEntries();
	return {
		entries
	};
};

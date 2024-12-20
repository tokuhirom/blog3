import type { PageServerLoad } from './$types';
import { EntryModel } from '$lib/db';

export const load: PageServerLoad = async () => {
	console.log('Loading entry page content!');

	const entries = await EntryModel.getAllEntries();
	return {
		entries
	};
};

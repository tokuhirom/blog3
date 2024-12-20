import type { PageServerLoad } from './$types';
import { EntryModel } from '$lib/db';

export const load: PageServerLoad = async ({ params }) => {
	console.log('Loading entry page content!');

	const path = params.path;

	const entry = await EntryModel.getEntry(path);
	console.log('Entry loaded: ', entry);
	return {
		entry: entry
	};
};

import type { PageServerLoad } from './$types';
import { EntryCache } from '$lib/cache';

export const load: PageServerLoad = async () => {
	console.log('Loading entry page content!');

	const entries = await EntryCache.get();
	return {
		entries
	};
};

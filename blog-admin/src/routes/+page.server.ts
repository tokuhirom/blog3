import type { PageServerLoad } from './$types';
import { PublicEntryRepository } from '$lib/db';
import { parse } from 'path';

export const load: PageServerLoad = async (params) => {
	console.log('Loading entry page content!');
    console.log(params);
    let page: string | null = params.url.searchParams.get('page')
    if (page === null || page === '' || page === undefined || parseInt(page, 10) < 1) {
        page = '1';
    }

	const { entries, hasNext } = await PublicEntryRepository.getPaginatedEntry(parseInt(page, 10), 30);
	return {
		entries,
        page: parseInt(page, 10),
        hasNext
	};
};

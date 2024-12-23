import type { PageServerLoad } from './$types';
import { PublicEntryRepository } from '$lib/db';

export const load: PageServerLoad = async (params) => {
	console.log('Loading top page content!');

	const page = getPage(params.url.searchParams.get('page'));

	const { entries, hasNext } = await PublicEntryRepository.getPaginatedEntry(page, 28);
	return {
		entries,
		page,
		hasNext
	};
};

function getPage(pageParam: string | null): number {
	let page = parseInt(pageParam || '1', 10);
	if (isNaN(page) || page < 1) {
		page = 1;
	}
	return page;
}

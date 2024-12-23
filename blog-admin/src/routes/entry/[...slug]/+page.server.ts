import type { PageServerLoad } from './$types';
import { PublicEntryRepository } from '$lib/db';

export const load: PageServerLoad = async (params) => {
    console.log('Loading entry page content!');
    const path = params.params.slug;

    const entry = await PublicEntryRepository.getEntry(path);

    return {
        entry,
    };
};

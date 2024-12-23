import type { PageServerLoad, Actions } from './$types';
import { redirect } from '@sveltejs/kit';

export const load: PageServerLoad = async ({ params, locals }) => {
	const path = params.path;

	const entry = await locals.adminEntryRepository.getEntry(path);
	return {
		entry: entry
	};
};

export const actions: Actions = {
	delete: async ({ params, locals }) => {
		const path = params.path;

		await locals.adminEntryRepository.deleteEntry(path);

		redirect(302, '/admin/');
	}
};

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
	update: async ({ request, params, locals }) => {
		const formData = await request.formData();
		const title = formData.get('title') as string;
		const body = formData.get('body') as string;
		const status = formData.get('status') as 'draft' | 'published';
		const path = params.path;

		// 必須フィールドチェック
		if (!title || !body || !status) {
			return { error: 'Missing required fields' };
		}

		console.log(
			`Updating entry with path: ${path}, title: ${title}, body: ${body}, status: ${status}`
		);
		await locals.adminEntryRepository.updateEntry(path, { title, body, status });
		return { success: true };
	},
	delete: async ({ params, locals }) => {
		const path = params.path;

		await locals.adminEntryRepository.deleteEntry(path);

		redirect(302, '/admin/');
	}
};

import type { PageServerLoad } from './$types';
import { EntryModel } from '$lib/db';
import { redirect } from '@sveltejs/kit';

export const load: PageServerLoad = async ({ params }) => {
	console.log('Loading entry page content!');

	const path = params.path;

	const entry = await EntryModel.getEntry(path);
	console.log('Entry loaded: ', entry);
	return {
		entry: entry
	};
};

export const actions: Actions = {
	update: async ({ request, params }) => {
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
			'Updating entry with path: ',
			path,
			' body=',
			body,
			' title=',
			title,
			' status=',
			status
		);
		await EntryModel.updateEntry(path, { title, body, status });
		return { success: true };
	},
	delete: async ({ params }) => {
		const path = params.path;

		await EntryModel.deleteEntry(path);

		redirect(302, '/');
	}
};

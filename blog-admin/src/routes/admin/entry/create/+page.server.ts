import { AdminEntryRepository } from '$lib/db';
import { redirect, type Actions } from '@sveltejs/kit';

export const actions: Actions = {
	create: async ({ request }) => {
		const formData = await request.formData();
		const title = formData.get('title') as string;
		const body = formData.get('body') as string;
		const status = formData.get('status') as 'draft' | 'published';

		// 必須フィールドチェック
		if (!title || !body || !status) {
			return { error: 'Missing required fields' };
		}
		await AdminEntryRepository.createEntry({ title, body, status });

		redirect(302, '/admin/');
	}
};

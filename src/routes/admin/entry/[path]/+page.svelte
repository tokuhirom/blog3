<script lang="ts">
	import type { Entry } from '$lib/db';
	import { formatDateForMySQL } from '$lib/mysqlutils';
	import { error } from '@sveltejs/kit';
	import type { PageData } from './$types';
	import { debounce } from '$lib/utils';

	import MarkdownEditor from '$lib/components/admin/MarkdownEditor.svelte';
	import { parseISO } from 'date-fns';

	let { data }: { data: PageData } = $props();
	if (!data.entry) {
		error(500, 'Missing entry data');
	}

	let entry: Entry = data.entry;
	let title: string = $state(entry.title);
	let body: string = $state(entry.body); // 初期値として本文を保持
	let status: 'draft' | 'published' = $state(entry.status);

	let successMessage = $state('');
	let errorMessage = $state('');

	async function handleDelete(event: Event) {
		event.preventDefault();

		const confirmed = confirm(`Are you sure you want to delete the entry "${title}"?`);
		if (confirmed) {
			successMessage = '';
			errorMessage = '';

			const response = await fetch('/admin/api/entry/' + entry.path, {
				method: 'DELETE'
			});
			if (response.ok) {
				successMessage = 'Entry deleted successfully';
				location.href = '/admin';
			} else {
				errorMessage = 'Failed to delete entry';
			}
		}
	}

	async function handleUpdate() {
		successMessage = '';
		errorMessage = '';

		try {
			const request = {
				title,
				body,
				status,
				updated_at: formatDateForMySQL(entry.updated_at)
			};
			const response = await fetch('/admin/api/entry/' + entry.path, {
				method: 'POST',
				headers: {
					'Content-Type': 'application/json'
				},
				body: JSON.stringify(request)
			});
			if (response.ok) {
				successMessage = 'Entry updated successfully';

				response.json().then((data) => {
					entry.updated_at = parseISO(data.updated_at);
				});
			} else {
				let errorDetails = 'Unknown error';
				try {
					const errorData = await response.json();
					if (errorData && errorData.error) {
						errorDetails = errorData.error;
					}
				} catch (e) {
					console.error('Failed to parse error response', e);
				}
				errorMessage = `Failed to update entry: ${response.statusText} (${response.status}) - ${errorDetails}`;
			}
		} catch (e) {
			errorMessage = 'Failed to update entry';
			console.error('Failed to update entry:', e);
		}
	}

	// デバウンスした自動保存関数
	const debouncedUpdate = debounce(() => {
		handleUpdate();
	}, 3000);

	// 入力イベントや変更イベントにデバウンスされた関数をバインド
	function handleInput() {
		debouncedUpdate();
	}
</script>

<form class="space-y-4 p-4">
	<div>
		<label for="title" class="block text-sm font-medium text-gray-700">Title</label>
		<input
			id="title"
			name="title"
			type="text"
			class="w-full rounded border p-2"
			bind:value={title}
			oninput={handleInput}
			required
		/>
	</div>

	<div class="editor">
		<label for="body" class="block text-sm font-medium text-gray-700">Body</label>
		<input type="hidden" name="body" bind:value={body} />
		<MarkdownEditor
			initialContent={body}
			onUpdateText={(content) => {
				body = content;
				handleInput(); // エディタ更新時もデバウンスされた更新をトリガー
			}}
		></MarkdownEditor>
	</div>

	<div>
		<label for="status" class="block text-sm font-medium text-gray-700">Status</label>
		<select
			id="status"
			name="status"
			class="w-full rounded border p-2"
			bind:value={status}
			onchange={handleInput}
		>
			<option value="draft">Draft</option>
			<option value="published">Published</option>
		</select>
	</div>

	<div class="flex justify-between">
		<button
			type="submit"
			class="rounded bg-red-500 px-4 py-2 text-white hover:bg-red-600"
			onclick={handleDelete}
		>
			Delete Entry
		</button>
	</div>

	<!-- link to the user side page -->
	{#if status === 'published'}
		<div class="flex justify-between p-3">
			<a
				href="/entry/{entry.path}"
				class="rounded bg-green-500 px-4 py-2 text-white hover:underline">Go to User Side Page</a
			>
		</div>
	{/if}
</form>

{#if successMessage}
	<p class="text-green-500">{successMessage}</p>
{/if}

{#if errorMessage}
	<p class="text-red-500">{errorMessage}</p>
{/if}

<style>
	.editor {
		border: 1px solid #d1d5db;
		border-radius: 0.25rem;
		height: 400px;
		overflow-y: scroll;
	}
</style>

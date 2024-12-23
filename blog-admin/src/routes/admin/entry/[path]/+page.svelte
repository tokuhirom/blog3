<script lang="ts">
	import type { Entry } from '$lib/db';
	import { error } from '@sveltejs/kit';
	import type { PageData } from './$types';

	import MarkdownEditor from '$lib/components/admin/MarkdownEditor.svelte';

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

	function handleDelete(event: Event) {
		event.preventDefault();

		const confirmed = confirm(`Are you sure you want to delete the entry "${title}"?`);
		if (confirmed) {
			const form = (event.target as HTMLElement).closest('form');
			if (form) {
				form.action = '?/delete';
				form.submit();
			} else {
				console.error('Form not found');
			}
		}
	}
</script>

<form
	method="post"
	action="?/update"
	class="space-y-4 p-4"
	on:submit={() => {
		successMessage = '';
		errorMessage = '';
	}}
>
	<div>
		<label for="title" class="block text-sm font-medium text-gray-700">Title</label>
		<input
			id="title"
			name="title"
			type="text"
			class="w-full rounded border p-2"
			bind:value={title}
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
				console.log(body);
			}}
		></MarkdownEditor>
	</div>

	<div>
		<label for="status" class="block text-sm font-medium text-gray-700">Status</label>
		<select id="status" name="status" class="w-full rounded border p-2" bind:value={status}>
			<option value="draft">Draft</option>
			<option value="published">Published</option>
		</select>
	</div>

	<div class="flex justify-between">
		<button type="submit" class="rounded bg-blue-500 px-4 py-2 text-white hover:bg-blue-600">
			Update Entry
		</button>

		<button
			type="submit"
			formaction="?/delete"
			class="rounded bg-red-500 px-4 py-2 text-white hover:bg-red-600"
			on:click|preventDefault={handleDelete}
		>
			Delete Entry
		</button>
	</div>

	<!-- link to the user side page -->
	<div class="flex justify-between p-3">
		<a href="/entry/{entry.path}" class="rounded bg-green-500 px-4 py-2 text-white hover:underline">Go to User Side Page</a>
	</div>
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

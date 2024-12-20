<script lang="ts">
	import type { Entry } from '$lib/db';
	import { error } from '@sveltejs/kit';
	import type { PageData } from './$types';

	let { data }: { data: PageData } = $props();
	if (!data.entry) {
		error(500, 'Missing entry data');
	}
	let entry: Entry = data.entry;
	let title: string = $state(entry.title);
	let body: string = $state(entry.body);
	let status: 'draft' | 'published' = $state(entry.status);

	let successMessage = $state('');
	let errorMessage = $state('');
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

	<div>
		<label for="body" class="block text-sm font-medium text-gray-700">Body</label>
		<textarea
			id="body"
			name="body"
			class="w-full rounded border p-2"
			rows="10"
			bind:value={body}
			required
		></textarea>
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
		>
			Delete Entry
		</button>
	</div>
</form>

{#if successMessage}
	<p class="text-green-500">{successMessage}</p>
{/if}

{#if errorMessage}
	<p class="text-red-500">{errorMessage}</p>
{/if}

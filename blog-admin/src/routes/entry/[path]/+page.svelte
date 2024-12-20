<script lang="ts">
	import type { Entry } from '$lib/db';
	import { error } from '@sveltejs/kit';
	import type { PageData } from './$types';

	let { data }: { data: PageData } = $props();
	if (!data.entry) {
        error(500, "Missing entry data");
	}
	let entry: Entry = data.entry;
	console.log(entry);
</script>

<div class="p-4">
	<h1 class="text-2xl font-bold">{entry.title}</h1>
	<p class="mb-4 text-gray-600">{entry.created_at}</p>
	<div class="whitespace-pre-line">{entry.body}</div>
</div>

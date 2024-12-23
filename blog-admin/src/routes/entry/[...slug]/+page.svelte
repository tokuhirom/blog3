<script lang="ts">
	import type { PageData } from './$types';

	export let data: PageData;
</script>

<div class="container mx-auto p-4 max-w-3xl bg-white rounded-lg shadow-lg">
	<h2 class="text-2xl font-bold text-gray-800">{data.entry.title}</h2>
	<p class="mt-4 text-gray-700 whitespace-pre-line">{data.entry.body}</p>
	<div class="mt-6 text-sm text-gray-500">
		<div>Created: <span class="font-medium">{data.entry.created_at}</span></div>
		{#if data.entry.updated_at !== null}
			<div>Updated: <span class="font-medium">{data.entry.updated_at}</span></div>
		{/if}
	</div>
</div>

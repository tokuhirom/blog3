<script lang="ts">
	import type { PageData } from './$types';

	export let data: PageData;
</script>

<svelte:head>
	<title>tokuhirom's blog</title>
</svelte:head>

<div class="container mx-auto p-4">
	<div class="grid grid-cols-1 gap-4 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4">
		{#each data.entries as entry}
			<a
				href="/entry/{entry.path}"
				class="block rounded-lg border border-gray-200 bg-white p-4 shadow transition-shadow hover:shadow-lg"
			>
				<h2 class="truncate text-lg font-semibold text-gray-800">{entry.title}</h2>
				<p class="mt-2 line-clamp-3 text-sm text-gray-600">{entry.body}</p>
			</a>
		{/each}
	</div>

	<nav class="pager mt-6 flex items-center justify-between">
		{#if data.page > 1}
			<a
				href="/?page={data.page - 1}"
				class="rounded bg-blue-500 px-4 py-2 text-white transition-colors hover:bg-blue-600"
			>
				Previous
			</a>
		{/if}
		{#if data.hasNext}
			<a
				href="/?page={data.page + 1}"
				class="rounded bg-blue-500 px-4 py-2 text-white transition-colors hover:bg-blue-600"
			>
				Next
			</a>
		{/if}
	</nav>
</div>

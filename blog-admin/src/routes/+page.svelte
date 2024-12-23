<script lang="ts">
	import type { PageData } from './$types';

	export let data: PageData;
</script>

<div class="container mx-auto p-4">
	<div class="grid grid-cols-1 gap-4 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4">
		{#each data.entries as entry}
			<a
				href="/entry/{entry.path}"
				class="block rounded-lg border border-gray-200 bg-white p-4 shadow hover:shadow-lg transition-shadow"
			>
				<h2 class="text-lg font-semibold text-gray-800 truncate">{entry.title}</h2>
				<p class="mt-2 text-sm text-gray-600 line-clamp-3">{entry.body}</p>
			</a>
		{/each}
	</div>

	<nav class="pager flex justify-between items-center mt-6">
		{#if data.page > 1}
			<a
				href="/?page={data.page - 1}"
				class="rounded bg-blue-500 px-4 py-2 text-white hover:bg-blue-600 transition-colors"
			>
				Previous
			</a>
		{/if}
		{#if data.hasNext}
			<a
				href="/?page={data.page + 1}"
				class="rounded bg-blue-500 px-4 py-2 text-white hover:bg-blue-600 transition-colors"
			>
				Next
			</a>
		{/if}
	</nav>
</div>

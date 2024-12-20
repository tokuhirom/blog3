<script lang="ts">
	import type { Entry } from '$lib/db';

	export let entries: Entry[] = [];
</script>

<div class="list">
	{#each entries as entry}
		<a
			class="item break-all shadow-md"
			href={`/entry/${encodeURIComponent(entry.path)}`}
			class:bg-gray-200={entry.status === 'draft'}
		>
			<h2 class="mb-2 text-sm font-bold">{entry.title}</h2>
			<p class="text-xs text-gray-600">{entry.body.slice(0, 100)}...</p>
		</a>
	{/each}
</div>

<style>
	.list {
		display: grid;
		grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
		gap: 1rem;
	}

	.item {
		width: 100%;
		padding: 1rem;
	}
</style>

<script lang="ts">
	import type { Entry } from '$lib/db';

	export let entries: Entry[] = [];
</script>

<div class="list">
	{#each entries as entry}
		<a
			class="item {entry.visibility === 'private' ? 'private' : ''}"
			href={`/admin/entry/${encodeURIComponent(entry.path)}`}
		>
			<h2 class="title">{entry.title}</h2>
			<p class="body">{entry.body.slice(0, 100)}...</p>
		</a>
	{/each}
</div>

<style>
	.list {
		display: flex;
		flex-wrap: wrap;
		gap: 1rem;
	}

	.item {
		flex: 1 1 150px;
		max-width: 150px;
		padding: 1rem;
		word-break: break-all;
		box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
	}

	.item.private {
		background-color: #e5e7eb;
	}

	.title {
		margin-bottom: 0.5rem;
		font-size: 0.875rem;
		font-weight: bold;
	}

	.body {
		font-size: 0.75rem;
		color: #4b5563;
	}
</style>

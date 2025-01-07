<script lang="ts">
	import PublicEntryCardItem from '$lib/components/public/PublicEntryCardItem.svelte';
	import type { PageData } from './$types';

	let { data }: { data: PageData } = $props();
</script>

<svelte:head>
	<title>tokuhirom's blog</title>
</svelte:head>

<div class="container">
	<div class="flex-container">
		{#each data.entries as entry (entry.path)}
			<PublicEntryCardItem {entry} />
		{/each}
	</div>

	<nav class="pager">
		{#if data.page > 1}
			<a href="/?page={data.page - 1}" class="pager-link"> Previous </a>
		{/if}
		{#if data.hasNext}
			<a href="/?page={data.page + 1}" class="pager-link"> Next </a>
		{/if}
	</nav>
</div>

<style>
	.container {
		max-width: 1080px;
		margin: 0 auto;
		padding: 1rem;
	}

	.flex-container {
		display: flex;
		flex-wrap: wrap;
		margin: auto;
		gap: 1rem;
		justify-content: flex-start;
	}

	.entry-link {
		flex: 1 1 180px;
		max-width: 162px;
		display: block;
		border-radius: 0.5rem;
		border: 1px solid #e2e8f0;
		background-color: white;
		padding: 1rem;
		transition: box-shadow 0.2s;
	}

	.entry-link:hover {
		box-shadow:
			0 10px 15px -3px rgba(0, 0, 0, 0.2),
			0 4px 6px -2px rgba(0, 0, 0, 0.1);
	}

	.entry-title {
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
		font-size: 1rem;
		font-weight: 600;
		color: #2d3748;
	}

	.entry-body {
		margin-top: 0.5rem;
		display: -webkit-box;
		line-clamp: 7;
		-webkit-line-clamp: 7;
		-webkit-box-orient: vertical;
		overflow: hidden;
		font-size: 0.875rem;
		color: #718096;
	}

	.pager {
		margin-top: 1.5rem;
		display: flex;
		align-items: center;
		justify-content: space-between;
	}

	.pager-link {
		border-radius: 0.25rem;
		background-color: #4299e1;
		padding: 0.5rem 1rem;
		color: white;
		transition: background-color 0.2s;
	}

	.pager-link:hover {
		background-color: #3182ce;
	}
</style>

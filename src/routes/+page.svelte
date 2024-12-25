<script lang="ts">
	import type { PageData } from './$types';

	export let data: PageData;
</script>

<svelte:head>
	<title>tokuhirom's blog</title>
</svelte:head>

<div class="container">
	<div class="grid">
		{#each data.entries as entry}
			<a href="/entry/{entry.path}" class="entry-link">
				<h2 class="entry-title">{entry.title}</h2>
				<p class="entry-body">{entry.body}</p>
			</a>
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
		margin: 0 auto;
		padding: 1rem;
	}
	.grid {
		display: grid;
		grid-template-columns: repeat(1, 1fr);
		gap: 1rem;
	}
	@media (min-width: 640px) {
		.grid {
			grid-template-columns: repeat(2, 1fr);
		}
	}
	@media (min-width: 768px) {
		.grid {
			grid-template-columns: repeat(3, 1fr);
		}
	}
	@media (min-width: 1024px) {
		.grid {
			grid-template-columns: repeat(4, 1fr);
		}
	}
	.entry-link {
		display: block;
		border-radius: 0.5rem;
		border: 1px solid #e2e8f0;
		background-color: white;
		padding: 1rem;
		box-shadow:
			0 10px 15px -3px rgba(0, 0, 0, 0.1),
			0 4px 6px -2px rgba(0, 0, 0, 0.05);
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
		font-size: 1.125rem;
		font-weight: 600;
		color: #2d3748;
	}
	.entry-body {
		margin-top: 0.5rem;
		display: -webkit-box;
		-webkit-line-clamp: 3;
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

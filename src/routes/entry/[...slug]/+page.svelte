<script lang="ts">
	import type { PageData } from './$types';
	import 'highlight.js/styles/github.css';

	export let data: PageData;
</script>

<svelte:head>
	<title>{data.entry.title} - tokuhirom's blog</title>
</svelte:head>

<div class="container">
	<h2 class="entry-title">{data.entry.title}</h2>
	<p class="entry-body">
		<!-- eslint-disable-next-line svelte/no-at-html-tags -->
		{@html data.body}
	</p>
	<div class="entry-meta">
		<div>Created: <span class="entry-date">{data.entry.created_at}</span></div>
		{#if data.entry.updated_at !== null}
			<div>Updated: <span class="entry-date">{data.entry.updated_at}</span></div>
		{/if}
	</div>
</div>

<style>
	.container {
		margin: 0 auto;
		max-width: 48rem;
		border-radius: 0.5rem;
		background-color: white;
		padding: 1rem;
		box-shadow:
			0 10px 15px -3px rgba(0, 0, 0, 0.1),
			0 4px 6px -2px rgba(0, 0, 0, 0.05);
	}
	.entry-title {
		font-size: 1.5rem;
		font-weight: bold;
		color: #2d3748;
	}
	.entry-body {
		margin-top: 1rem;
		white-space: pre-line;
		color: #4a5568;
	}
	.entry-meta {
		margin-top: 1.5rem;
		font-size: 0.875rem;
		color: #a0aec0;
	}
	.entry-date {
		font-weight: 500;
	}
</style>

<script lang="ts">
	import type { PageData } from './$types';
	import 'highlight.js/styles/github.css';

	export let data: PageData;
</script>

<svelte:head>
	<title>{data.entry.title} - tokuhirom's blog</title>
</svelte:head>

<div class="container mx-auto max-w-3xl rounded-lg bg-white p-4 shadow-lg">
	<h2 class="text-2xl font-bold text-gray-800">{data.entry.title}</h2>
	<p class="prose mt-4 whitespace-pre-line text-gray-700">
		<!-- eslint-disable-next-line svelte/no-at-html-tags -->
		{@html data.body}
	</p>
	<div class="mt-6 text-sm text-gray-500">
		<div>Created: <span class="font-medium">{data.entry.created_at}</span></div>
		{#if data.entry.updated_at !== null}
			<div>Updated: <span class="font-medium">{data.entry.updated_at}</span></div>
		{/if}
	</div>
</div>

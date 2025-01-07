<script lang="ts">
	import PublicEntryCardItem from '$lib/components/public/PublicEntryCardItem.svelte';
	import type { PageData } from './$types';

	let { data }: { data: PageData } = $props();
</script>

<svelte:head>
	<title>tokuhirom's blog</title>
	<script
		async
		src="https://pagead2.googlesyndication.com/pagead/js/adsbygoogle.js?client=ca-pub-9032322815824634"
		crossorigin="anonymous"
	></script>
</svelte:head>

<div class="container">
	<div class="flex-container">
		{#each data.entries as entry (entry.path)}
			<PublicEntryCardItem {entry} />
		{/each}
	</div>

	<nav class="pager">
		{#if data.page > 1}
			<a href="/?page={data.page - 1}" class="pager-link pager-link-prev"> Previous </a>
		{/if}
		{#if data.hasNext}
			<a href="/?page={data.page + 1}" class="pager-link pager-link-next"> Next </a>
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

	.pager {
		margin-top: 1.5rem;
		display: flex;
		align-items: center;
		justify-content: space-between; /* これで左右に配置される */
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

	.pager-link-prev {
		margin-right: auto; /* 左寄せ */
	}

	.pager-link-next {
		margin-left: auto; /* 右寄せ */
	}
</style>

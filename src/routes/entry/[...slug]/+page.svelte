<script lang="ts">
	import type { PageData } from './$types';
	import 'highlight.js/styles/github.css';
	import PublicLinkPallet from './PublicLinkPallet.svelte';

	let { data }: { data: PageData } = $props();
</script>

<svelte:head>
	<title>{data.entry.title} - tokuhirom's blog</title>
	<script
		async
		src="https://pagead2.googlesyndication.com/pagead/js/adsbygoogle.js?client=ca-pub-9032322815824634"
		crossorigin="anonymous"
	></script>
</svelte:head>

<div class="container">
	<div class="container-wrapper">
		<h2 class="entry-title">{data.entry.title}</h2>
		<p class="entry-body">
			<!-- eslint-disable-next-line svelte/no-at-html-tags -->
			{@html data.body}
		</p>
		<div class="entry-meta">
			<div>Published: <span class="entry-date">{data.entry.published_at}</span></div>
			<div>Updated: <span class="entry-date">{data.entry.last_edited_at}</span></div>
		</div>
	</div>
	<div class="link-pallet">
		<PublicLinkPallet linkPallet={data.linkPallet} />
	</div>
</div>

<style>
	.container {
		margin: 0 auto;
		max-width: 1200px;
		margin-top: 40px;
	}
	.container-wrapper {
		margin: 0 auto;
		border-radius: 0.5rem;
		background-color: white;
		padding: 1rem;
		box-shadow:
			0 10px 15px -3px rgba(0, 0, 0, 0.1),
			0 4px 6px -2px rgba(0, 0, 0, 0.05);
	}
	.link-pallet {
		margin-top: 1rem;
	}
	.entry-title {
		font-size: 1.5rem;
		font-weight: bold;
		color: #2d3748;
	}
	.entry-body {
		margin-top: 1rem;
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

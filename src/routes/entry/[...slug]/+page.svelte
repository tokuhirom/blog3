<script lang="ts">
	import { onMount } from 'svelte';
	import type { PageData } from './$types';
	import 'highlight.js/styles/github.css';

	export let data: PageData;

	onMount(() => {
		const links = document.querySelectorAll('.entry-link');
		links.forEach((link: Element) => {
			(link as HTMLElement).addEventListener('click', handleEntryLinkClick);
		});

		// クリーンアップ処理
		return () => {
			links.forEach((link) => {
				(link as HTMLElement).removeEventListener('click', handleEntryLinkClick);
			});
		};
	});

	function handleEntryLinkClick(event: MouseEvent) {
		event.preventDefault();

		const target = event.target as HTMLElement | null;
		if (target && target instanceof HTMLElement) {
			const link = target.closest('.entry-link') as HTMLAnchorElement | null;

			if (link) {
				const title = link.dataset.title; // data-title 属性から取得
				if (!title) {
					console.warn('No data-title attribute found');
					return;
				}

				console.log(`Entry link clicked: '${title}'`);

				// API 呼び出しなどの処理をここで実行
				fetch(`/api/entry/by-title/${encodeURIComponent(title)}`)
					.then(async (response) => {
						if (response.status === 404) {
							link.classList.add('entry-not-found');
						} else {
							const data = await response.json();
							const path = data.path;
							if (path) {
								window.location.href = `/entry/${path}`;
							} else {
								console.error('No path found in response data');
							}
						}
					})
					.catch((err) => {
						console.error('Error fetching entry data:', err);
					});
			} else {
				console.warn('No .entry-link element found');
			}
		} else {
			console.warn('Event target is not an HTML element');
		}
	}
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

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

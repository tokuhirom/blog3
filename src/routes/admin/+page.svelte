<script lang="ts">
	import EntryList from './EntryList.svelte';
	import SearchBox from './SearchBox.svelte';
	import type { Entry } from '$lib/db';
	import type { PageData } from './$types';
	import { error } from '@sveltejs/kit';
	import { onMount, onDestroy } from 'svelte';

	let searchKeyword = '';
	let { data }: { data: PageData } = $props();

	if (!data.entries) {
		error(500, 'Missing entries data');
	}

	let allEntries: Entry[] = $state(data.entries);
	let filteredEntries: Entry[] = $state(data.entries);

	let isLoading = $state(false);
	let hasMore = $state(true);
	let loadInterval: ReturnType<typeof setInterval> | null = null;

	function handleSearch(keyword: string) {
		searchKeyword = keyword.toLowerCase();
		filteredEntries = allEntries.filter(
			(entry) =>
				entry.title.toLowerCase().includes(searchKeyword) ||
				entry.body.toLowerCase().includes(searchKeyword)
		);
	}

	async function loadMoreEntries() {
		if (isLoading || !hasMore) return;

		isLoading = true;

		const lastPath = allEntries[allEntries.length - 1]?.path;
		if (!lastPath) {
			isLoading = false;
			hasMore = false;
			return;
		}

		try {
			const response = await fetch(`/admin/api/entry?last_path=${encodeURIComponent(lastPath)}`);
			if (!response.ok) {
				throw new Error('Failed to load more entries');
			}

			const newEntries: Entry[] = await response.json();
			if (newEntries.length === 0) {
				hasMore = false;
			} else {
				allEntries = [...allEntries, ...newEntries];
				handleSearch(searchKeyword);
			}
		} catch (err) {
			console.error(err);
		} finally {
			isLoading = false;
		}
	}

	onMount(() => {
		loadInterval = setInterval(() => {
			if (!isLoading && hasMore) {
				loadMoreEntries();
			}
		}, 10);

		return () => {
			if (loadInterval) {
				clearInterval(loadInterval);
			}
		};
	});

	onDestroy(() => {
		if (loadInterval) {
			clearInterval(loadInterval);
		}
	});
</script>

<div class="container">
	<SearchBox onSearch={handleSearch} />
	<EntryList entries={filteredEntries} />
	{#if isLoading || hasMore}
		<p class="loading-message">Loading more entries...</p>
	{/if}
	{#if !hasMore && allEntries.length > 0}
		<p class="loading-message">No more entries to load</p>
	{/if}
</div>

<style>
	.container {
		padding: 1rem;
	}

	.loading-message {
		margin-top: 1rem;
		text-align: center;
		color: #6b7280;
	}
</style>

<script lang="ts">
	import SearchBox from './SearchBox.svelte';
	import { type Entry, type EntryImageAware } from '$lib/entity';
	import type { PageData } from './$types';
	import { error } from '@sveltejs/kit';
	import { onMount, onDestroy } from 'svelte';
	import AdminEntryCardItem from './AdminEntryCardItem.svelte';

	let searchKeyword = $state('');
	let { data }: { data: PageData } = $props();

	if (!data.entries) {
		error(500, 'Missing entries data');
	}

	let allEntries: (Entry & EntryImageAware)[] = $state(data.entries);
	let filteredEntries: (Entry & EntryImageAware)[] = $derived.by(() => {
		if (searchKeyword === '') {
			return allEntries;
		}

		const lowerKeyword = searchKeyword.toLowerCase();
		return allEntries.filter(
			(entry) =>
				entry.title.toLowerCase().includes(lowerKeyword) ||
				entry.body.toLowerCase().includes(lowerKeyword)
		);
	});

	let isLoading = $state(false);
	let hasMore = $state(true);
	let loadInterval: ReturnType<typeof setInterval> | null = null;

	function handleSearch(keyword: string) {
		searchKeyword = keyword;
	}

	async function loadMoreEntries() {
		if (isLoading || !hasMore) return;

		isLoading = true;

		const last_last_edited_at = allEntries[allEntries.length - 1]?.last_edited_at;
		if (!last_last_edited_at) {
			isLoading = false;
			hasMore = false;
			return;
		}

		try {
			const response = await fetch(
				`/admin/api/entry?last_last_edited_at=${encodeURIComponent(last_last_edited_at)}`
			);
			if (!response.ok) {
				throw new Error('Failed to load more entries');
			}

			const newEntries: (Entry & EntryImageAware)[] = await response.json();
			if (newEntries.length === 0) {
				hasMore = false;
			} else {
				const existingPaths = allEntries.map((entry) => entry.path);
				const addingNewEntries = newEntries.filter((entry) => !existingPaths.includes(entry.path));
				if (addingNewEntries.length == 0) {
					console.log(
						`All entries are duplicated... stopping loading more entries. last_last_edited_at=${last_last_edited_at}, newEntries=${newEntries.map((entry) => entry.title)}`
					);
					hasMore = false;
				} else {
					allEntries = [...allEntries, ...addingNewEntries];
				}
			}
		} catch (err) {
			console.error(err);
		} finally {
			isLoading = false;
		}
	}

	async function handleKeydown(event: KeyboardEvent) {
		if (event.key === 'c' && !event.ctrlKey && !event.altKey && !event.metaKey && !event.shiftKey) {
			event.preventDefault();
			event.stopPropagation();
			try {
				const response = await fetch('/admin/api/entry', {
					method: 'POST',
					headers: {
						'Content-Type': 'application/json'
					},
					body: JSON.stringify({})
				});
				if (response.ok) {
					const data = await response.json();
					location.href = `/admin/entry/${data.path}`;
				} else {
					alert(`Failed to create new entry: ${response.status} ${response.statusText}`);
				}
			} catch (err) {
				console.error(err);
				alert(`Failed to create new entry: ${err}`);
				return false;
			}
		}
		return true;
	}

	onMount(() => {
		window.addEventListener('keydown', handleKeydown);
		loadInterval = setInterval(() => {
			if (!isLoading && hasMore) {
				loadMoreEntries();
			}
		}, 10);

		return () => {
			if (loadInterval) {
				clearInterval(loadInterval);
			}
			window.removeEventListener('keydown', handleKeydown);
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
	<div class="entry-list">
		{#each filteredEntries as entry (entry.path)}
			<AdminEntryCardItem {entry} />
		{/each}
	</div>
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
		margin: 0 auto;
		max-width: 1200px;
	}

	.loading-message {
		margin-top: 1rem;
		text-align: center;
		color: #6b7280;
	}

	.entry-list {
		display: flex;
		flex-wrap: wrap;
		margin: auto;
		gap: 1rem;
		justify-content: flex-start;
		max-width: 1200px;
	}
</style>

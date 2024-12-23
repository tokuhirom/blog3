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

	// ロード状態を管理
	let isLoading = $state(false);
	let hasMore = $state(true); // まだエントリが残っているかどうか
	let loadInterval: ReturnType<typeof setInterval> | null = null;

	// 検索時の処理
	function handleSearch(keyword: string) {
		searchKeyword = keyword.toLowerCase(); // case insensitive のため小文字化
		filteredEntries = allEntries.filter(
			(entry) =>
				entry.title.toLowerCase().includes(searchKeyword) ||
				entry.body.toLowerCase().includes(searchKeyword)
		);
	}

	// 新しいエントリを取得する関数
	async function loadMoreEntries() {
		if (isLoading || !hasMore) return;

		isLoading = true;

		// 最後のエントリのパスを取得
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
				hasMore = false; // 追加エントリなし
			} else {
				allEntries = [...allEntries, ...newEntries];
				handleSearch(searchKeyword); // フィルタリングを再適用
			}
		} catch (err) {
			console.error(err);
		} finally {
			isLoading = false;
		}
	}

	// 一定間隔でエントリをロード
	onMount(() => {
		loadInterval = setInterval(() => {
			if (!isLoading && hasMore) {
				loadMoreEntries();
			}
		}, 10); // 10ms 間隔でロード

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

<div class="p-4">
	<SearchBox onSearch={handleSearch} />
	<EntryList entries={filteredEntries} />
	{#if isLoading || hasMore}
		<p class="mt-4 text-center text-gray-500">Loading more entries...</p>
	{/if}
	{#if !hasMore && allEntries.length > 0}
		<p class="mt-4 text-center text-gray-500">No more entries to load</p>
	{/if}
</div>

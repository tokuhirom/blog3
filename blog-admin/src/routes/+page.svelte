<script lang="ts">
	import EntryList from './EntryList.svelte';
	import SearchBox from './SearchBox.svelte';
	import type { Entry } from '$lib/db';
	import type { PageData } from './$types';
	import { error } from '@sveltejs/kit';
	import { onMount } from 'svelte';

	let searchKeyword = '';
	let { data }: { data: PageData } = $props();

	if (!data.entries) {
		error(500, 'Missing entries data');
	}

	let allEntries: Entry[] = data.entries;
	let filteredEntries: Entry[] = $state(allEntries);

	// ロード状態を管理
	let isLoading = false;
	let hasMore = true; // まだエントリが残っているかどうか

	// 検索時の処理
	function handleSearch(keyword: string) {
		searchKeyword = keyword.toLowerCase(); // case insensitive のため小文字化
		filteredEntries = allEntries.filter(
			(entry) =>
				entry.title.toLowerCase().includes(searchKeyword) ||
				entry.body.toLowerCase().includes(searchKeyword)
		);
	}

	// スクロール時の処理
	async function handleScroll() {
		if (isLoading || !hasMore) return;

		const scrollPosition = window.scrollY + window.innerHeight;
		const documentHeight = document.body.scrollHeight;

		// スクロール位置が70%を超えた場合
		if (scrollPosition / documentHeight > 0.7) {
			isLoading = true;

			// 最後のエントリのパスを取得
			const lastPath = allEntries[allEntries.length - 1]?.path;
			if (!lastPath) {
				isLoading = false;
				return;
			}

			// 新しいエントリを取得
			try {
				const response = await fetch(`/api/entry?last_path=${encodeURIComponent(lastPath)}`);
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
	}

	onMount(() => {
		window.addEventListener('scroll', handleScroll);
		return () => {
			window.removeEventListener('scroll', handleScroll);
		};
	});
</script>

<div class="p-4">
	<SearchBox onSearch={handleSearch} />
	<EntryList entries={filteredEntries} />
	{#if isLoading}
		<p class="text-center text-gray-500 mt-4">Loading more entries...</p>
	{/if}
	{#if !hasMore && allEntries.length > 0}
		<p class="text-center text-gray-500 mt-4">No more entries to load</p>
	{/if}
</div>

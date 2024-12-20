<script lang="ts">
	import { onMount } from 'svelte';
	import EntryList from './EntryList.svelte';
	import SearchBox from './SearchBox.svelte';
	import type { Entry } from '$lib/db';

	let allEntries: Entry[] = [];
	let filteredEntries: Entry[] = [];
	let searchKeyword = '';

	async function fetchEntries() {
		const res = await fetch(`/api/entry`);
		const body = await res.json();
		allEntries = body.entries;
		filteredEntries = allEntries;
	}

	// 初期データ取得
	onMount(() => fetchEntries());

	// 検索時の処理
	function handleSearch(keyword: string) {
		searchKeyword = keyword.toLowerCase(); // case insensitive のため小文字化
		filteredEntries = allEntries.filter(
			(entry) =>
				entry.title.toLowerCase().includes(searchKeyword) ||
				entry.body.toLowerCase().includes(searchKeyword)
		);
	}
</script>

<div class="p-4">
	<!-- 検索ボックス -->
	<SearchBox onSearch={handleSearch} />

	<!-- エントリ一覧 -->
	<EntryList entries={filteredEntries} />
</div>

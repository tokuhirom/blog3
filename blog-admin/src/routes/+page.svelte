<script lang="ts">
	import EntryList from './EntryList.svelte';
	import SearchBox from './SearchBox.svelte';
	import type { Entry } from '$lib/db';
	import type { PageData } from './$types';
	import { error } from '@sveltejs/kit';

	let searchKeyword = '';

	let { data }: { data: PageData } = $props();
	if (!data.entries) {
		error(500, 'Missing entries data');
	}
	let allEntries: Entry[] = data.entries;
	let filteredEntries: Entry[] = $state(allEntries);

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
	<SearchBox onSearch={handleSearch} />
	<EntryList entries={filteredEntries} />
</div>

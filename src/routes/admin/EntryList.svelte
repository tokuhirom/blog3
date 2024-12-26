<script lang="ts">
	import type { Entry } from '$lib/db';
	import EntryCardItem from './EntryCardItem.svelte';

	export let entries: Entry[] = [];
</script>

<div class="list">
	{#each entries as entry}
		<EntryCardItem {entry} />
	{/each}
</div>

<style>
	.list {
		display: flex;
		flex-wrap: wrap;
		gap: 1rem;
	}
</style>

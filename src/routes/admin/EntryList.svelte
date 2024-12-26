<script lang="ts">
	import type { Entry } from '$lib/db';
	import EntryCard from './EntryCard.svelte';

	export let entries: Entry[] = [];
</script>

<div class="list">
	{#each entries as entry}
		<EntryCard {entry} />
	{/each}
</div>

<style>
	.list {
		display: flex;
		flex-wrap: wrap;
		gap: 1rem;
	}
</style>

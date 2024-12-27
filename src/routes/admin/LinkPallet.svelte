<script lang="ts">
	import EntryCardItem from './EntryCardItem.svelte';
	import CardItem from './CardItem.svelte';
	import { type LinkPalletData } from '$lib/repository/AdminEntryRepository';

	export let linkPallet: LinkPalletData;
</script>

<div class="link-pallet">
	<div class="one-hop-link">
		{#each linkPallet.links as link}
			<EntryCardItem entry={link} />
		{/each}
	</div>
	{#each linkPallet.twohops as twohops}
		<div class="two-hop-link">
			{#if twohops.src.title}
				<EntryCardItem entry={twohops.src} backgroundColor={'yellowgreen'} />
			{:else}
				<CardItem
					onClick={() => alert('TODO: create new entry. Not implemented yet.')}
					title={twohops.src.dst_title}
					content=""
					backgroundColor="#c0f6f6"
					color="gray"
				/>
			{/if}
			{#each twohops.links as link}
				<EntryCardItem entry={link} />
			{/each}
		</div>
	{/each}
	{#if linkPallet.newLinks.length > 0}
		<div class="one-hop-link">
			<CardItem onClick={() => false} title="New Item" content="" backgroundColor="darkgoldenrod" />
			{#each linkPallet.newLinks as title}
				<CardItem
					onClick={() => alert('TODO: create new entry. Not implemented yet.')}
					{title}
					content=""
					color="gray"
				/>
			{/each}
		</div>
	{/if}
</div>

<style>
	.one-hop-link {
		display: flex;
		flex-wrap: wrap;
		gap: 1rem;
		clear: both;
	}
	.two-hop-link {
		display: flex;
		flex-wrap: wrap;
		gap: 1rem;
		clear: both;
		margin-top: 1rem;
	}
</style>

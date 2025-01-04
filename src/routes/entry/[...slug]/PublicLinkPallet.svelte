<script lang="ts">
	import PublicEntryCardItem from '$lib/components/public/PublicEntryCardItem.svelte';
	import { type LinkPalletData } from '$lib/LinkPallet';

	export let linkPallet: LinkPalletData;
</script>

<div class="link-pallet">
	<div class="one-hop-link">
		{#each linkPallet.links as link}
			<PublicEntryCardItem entry={link} />
		{/each}
	</div>

	{#each linkPallet.twohops as twohop}
		<div class="two-hop-link">
			<PublicEntryCardItem entry={twohop.src} backgroundColor={'yellowgreen'} />
			{#each twohop.links as link}
				<PublicEntryCardItem entry={link} />
			{/each}
		</div>
	{/each}
</div>

<style>
	.one-hop-link {
		display: flex;
		flex-wrap: wrap;
		gap: 1rem;
	}

	.two-hop-link {
		display: flex;
		flex-wrap: wrap;
		gap: 1rem;
		clear: both;
		margin-top: 1rem;
	}
</style>

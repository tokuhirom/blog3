<script lang="ts">
	import CardItem from '../../lib/components/common/CardItem.svelte';
	import { type LinkPalletData } from '$lib/LinkPallet';
	import AdminEntryCardItem from './AdminEntryCardItem.svelte';

	let { linkPallet }: { linkPallet: LinkPalletData } = $props();

	function createNewEntry(title: string) {
		fetch('/admin/api/entry', {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json'
			},
			body: JSON.stringify({ title })
		})
			.then(async (response) => {
				if (response.ok) {
					const data = await response.json();
					location.href = `/admin/entry/${data.path}`;
				} else {
					console.error('Failed to create new entry');
					alert('Failed to create new entry. Cannot get new entry path');
				}
			})
			.catch((err) => {
				console.error('Error creating new entry:', err);
				alert('Failed to create new entry');
			});
	}
</script>

<div class="link-pallet">
	<div class="one-hop-link">
		{#each linkPallet.links as link}
			<AdminEntryCardItem entry={link} />
		{/each}
	</div>
	{#each linkPallet.twohops as twohops}
		<div class="two-hop-link">
			{#if twohops.src.title}
				<AdminEntryCardItem entry={twohops.src} backgroundColor={'yellowgreen'} />
			{:else}
				<CardItem
					onClick={() => createNewEntry(twohops.src.dst_title)}
					title={twohops.src.dst_title}
					content=""
					backgroundColor="#c0f6f6"
					color="gray"
				/>
			{/if}
			{#each twohops.links as link}
				<AdminEntryCardItem entry={link} />
			{/each}
		</div>
	{/each}
	{#if linkPallet.newLinks.length > 0}
		<div class="one-hop-link">
			<CardItem onClick={() => false} title="New Item" content="" backgroundColor="darkgoldenrod" />
			{#each linkPallet.newLinks as title}
				<CardItem onClick={() => createNewEntry(title)} {title} content="" color="gray" />
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

<script lang="ts">
	import type { Entry } from '$lib/db';

	export let entry: Entry;
	export let borderColor: string | undefined = undefined;
</script>

<a
	class="item {entry.visibility === 'private' ? 'private' : ''} {entry.body ? '' : 'no-body'}"
	href={`/admin/entry/${entry.path}`}
	style={borderColor ? `border: 1px solid ${borderColor}` : ''}
>
	<h2 class="title">{entry.title}</h2>
	{#if entry.body}
		<p class="body">{entry.body.slice(0, 100)}...</p>
	{/if}
</a>

<style>
	/*
	.list {
		display: flex;
		flex-wrap: wrap;
		gap: 1rem;
	}
	*/

	.item.no-body {
		opacity: 0.5;
	}

	.item {
		flex: 1 1 150px;
		max-width: 150px;
		padding: 1rem;
		word-break: break-all;
		/* box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1); */
		border: 1px solid #d1d5db;
		border-radius: 4px;
	}

	.item.private {
		background-color: #e5e7eb;
	}

	.title {
		margin-bottom: 0.5rem;
		font-size: 0.875rem;
		font-weight: bold;
	}

	.body {
		font-size: 0.75rem;
		color: #4b5563;
	}
</style>

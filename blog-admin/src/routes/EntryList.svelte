<script lang="ts">
	import type { Entry } from '$lib/db';

	export let entries: Entry[] = [];
</script>

<div class="grid grid-cols-2 gap-4 sm:grid-cols-3 md:grid-cols-4">
	{#each entries as entry}
		<a
			class="w-full max-w-[200px] break-all rounded bg-white p-4 shadow-md"
			href={`/entry/${encodeURIComponent(entry.path)}`}
		>
			<h2 class="mb-2 font-bold">{entry.title}</h2>
			<p class="text-sm text-gray-600">{entry.body.slice(0, 100)}...</p>
		</a>
	{/each}
</div>

<script lang="ts">
	import type { PageData } from './$types';

	export let data: PageData;
</script>

<div>
    <h2>{data.entry.title}</h2>
    <p>{data.entry.body}</p>
    <div>Created: {data.entry.created_at}</div>
    {#if data.entry.updated_at !== null}
        <div>Updated: {data.entry.updated_at}</div>
    {/if}
</div>
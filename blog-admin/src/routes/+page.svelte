<script lang="ts">
    	import type { PageData } from './$types';
	
	export let data: PageData;
</script>

<div>
    <div>
        {#each data.entries as entry}
            <a href="/entry/{entry.path}">
                <h2>{entry.title}</h2>
                <p>{entry.body}</p>
            </a>
        {/each}
    </div>

    <nav class="pager">
        {#if data.page > 1}
            <a href="/?page={data.page - 1}">Previous</a>
        {/if}
        {#if data.hasNext}
            <a href="/?page={data.page + 1}">Next</a>
        {/if}
    </nav>
</div>

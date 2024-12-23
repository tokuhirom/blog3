<script lang="ts">
	import '../app.css';
	import PublicHeader from '$lib/components/public/PublicHeader.svelte';

	let { children } = $props();
</script>


<svelte:head>
    <link rel="alternate" type="application/rss+xml" title="RSS Feed" href="https://blog.64p.org/feed" />
</svelte:head>

<div>
	<PublicHeader />
	<div class="pt-16">
		{@render children()}
	</div>
</div>

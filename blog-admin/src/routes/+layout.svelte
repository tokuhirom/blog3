<script lang="ts">
	import '../app.css';
	import PublicHeader from '$lib/components/public/PublicHeader.svelte';

	let { children } = $props();
</script>

<div>
	<PublicHeader />
	<div class="pt-16">
		{@render children()}
	</div>
</div>

<script lang="ts">
	import '../../app.css';
	import AdminHeader from '$lib/components/admin/AdminHeader.svelte';

	let { children } = $props();
</script>

<svelte:head>
	<title>tokuhirom's blog admin site</title>
</svelte:head>

<div>
	<AdminHeader />
	<div class="content">
		{@render children()}
	</div>
</div>

<style>
	.content {
		padding-top: 0.5rem;
	}
</style>

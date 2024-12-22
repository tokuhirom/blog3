<script lang="ts">
	import '../../app.css';
	import AdminHeader from '$lib/components/admin/AdminHeader.svelte';

	let { children } = $props();
</script>

<div>
	<AdminHeader />
	<div class="pt-16">
		{@render children()}
	</div>
</div>

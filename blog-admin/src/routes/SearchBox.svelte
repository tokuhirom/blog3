<script lang="ts">
	let { onSearch } = $props();
	let keyword = '';

	function onInput(event: Event) {
		const target = event.target as HTMLInputElement;
		if (target) {
			keyword = target.value;
		}
		onSearch(keyword);
	}
</script>

<input
	type="text"
	placeholder="Search entries..."
	class="mb-4 w-full rounded border p-2"
	on:input={onInput}
/>

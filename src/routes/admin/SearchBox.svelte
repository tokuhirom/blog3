<script lang="ts">
	import { debounce } from '$lib/utils';
	let { onSearch } = $props();
	let keyword = '';

	const debouncedSearch = debounce((newKeyword: string) => {
		onSearch(newKeyword);
	}, 1000);

	function onInput(event: Event) {
		const target = event.target as HTMLInputElement;
		if (target) {
			keyword = target.value;
		}
		debouncedSearch(keyword);
	}
</script>

<input
	type="text"
	placeholder="Search entries..."
	class="mb-4 w-full rounded border p-2"
	oninput={onInput}
/>

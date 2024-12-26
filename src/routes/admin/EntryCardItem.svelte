<script lang="ts">
	import type { Entry } from '$lib/db';
	import CardItem from './CardItem.svelte';

	export let entry: Entry;
	export let backgroundColor = entry.visibility == 'private' ? '#cccccc' : '#f6f6f6';
	export let color = '#0f0f0f';
	export let onClick: () => void = function () {
		location.href = '/admin/entry/' + entry.path;
	};

	let title = entry.title;
	let content = entry.body ? entry.body.slice(0, 100) + '...' : '';
	let imgSrc = undefined;
</script>

<CardItem {onClick} {backgroundColor} {color} {title} {content} {imgSrc} />

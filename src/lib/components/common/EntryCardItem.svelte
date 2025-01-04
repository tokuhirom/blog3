<script lang="ts">
	import { type Entry, type EntryImageAware } from '$lib/entity';
	import CardItem from './CardItem.svelte';

	export let entry: Entry & EntryImageAware;
	export let backgroundColor = entry.visibility == 'private' ? '#cccccc' : '#f6f6f6';
	export let color = '#0f0f0f';
	export let onClick: (event: MouseEvent) => void;

	let title = entry.title;
	let content = entry.body ? entry.body.slice(0, 100) + '...' : '';
	let imgSrc = entry.image_url;
</script>

<CardItem {onClick} {backgroundColor} {color} {title} {content} {imgSrc} />

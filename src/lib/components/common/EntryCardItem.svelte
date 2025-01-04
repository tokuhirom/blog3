<script lang="ts">
	import { type Entry, type EntryImageAware } from '$lib/entity';
	import CardItem from './CardItem.svelte';

	let {
		entry,
		backgroundColor = entry.visibility == 'private' ? '#cccccc' : '#f6f6f6',
		color = '#0f0f0f',
		onClick
	}: {
		entry: Entry & EntryImageAware;
		backgroundColor: string;
		color: string;
		onClick: (event: MouseEvent) => void;
	} = $props();

	let title = entry.title;
	let content = entry.body ? entry.body.slice(0, 100) + '...' : '';
	let imgSrc = entry.image_url;
</script>

<CardItem {onClick} {backgroundColor} {color} {title} {content} {imgSrc} />

<script lang="ts">
	import { type Entry } from '$lib/entity';
	import CardItem from './CardItem.svelte';

	export let entry: Entry;
	export let backgroundColor = entry.visibility == 'private' ? '#cccccc' : '#f6f6f6';
	export let color = '#0f0f0f';
	export let onClick: (event: MouseEvent) => void = function (event: MouseEvent) {
		if (event.metaKey || event.ctrlKey) {
			// Commandキー (Mac) または Ctrlキー (Windows/Linux) が押されている場合、別タブで開く
			window.open('/admin/entry/' + entry.path, '_blank');
		} else {
			// 通常クリック時は同じタブで開く
			location.href = '/admin/entry/' + entry.path;
		}
	};

	let title = entry.title;
	let content = entry.body ? entry.body.slice(0, 100) + '...' : '';
	let imgSrc = undefined;
</script>

<CardItem {onClick} {backgroundColor} {color} {title} {content} {imgSrc} />

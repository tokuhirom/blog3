<script lang="ts">
	import { type Entry, type EntryImageAware } from '$lib/entity';
	import EntryCardItem from '../common/EntryCardItem.svelte';

	export let entry: Entry & EntryImageAware;
	export let backgroundColor = entry.visibility == 'private' ? '#cccccc' : '#f6f6f6';
	export let color = '#0f0f0f';
	export let onClick: (event: MouseEvent) => void = (event: MouseEvent) => {
		if (event.metaKey || event.ctrlKey) {
			// Commandキー (Mac) または Ctrlキー (Windows/Linux) が押されている場合、別タブで開く
			window.open('/entry/' + entry.path, '_blank');
		} else {
			// 通常クリック時は同じタブで開く
			location.href = '/entry/' + entry.path;
		}
	};

	if (entry.visibility == 'private') {
		alert("[FATAL ERROR] This entry is private. You can't see this entry.");
	}
</script>

<EntryCardItem {onClick} {backgroundColor} {color} {entry} />

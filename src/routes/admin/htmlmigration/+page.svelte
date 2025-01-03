<script lang="ts">
	import html2md from 'html-to-md';
	import type { PageData } from './$types';
	import { error } from '@sveltejs/kit';
	import { renderHTML } from '$lib/markdown';
	import { type Entry } from '$lib/db';

	// Get the list of HTML formatted pages.
	let { data }: { data: PageData } = $props();

	if (!data.entries) {
		error(500, 'Missing entries data');
	}
	let entries: Entry[] = $state(data.entries);

	function handleConvert(event: Event, entry: Entry) {
		event.preventDefault();
		doConvert(entry);
	}

	function doConvert(entry: Entry) {
		entries = entries.filter((it: Entry) => it.path !== entry.path);

		fetch(`/admin/htmlmigration/${entry.path}/commit`, {
			method: 'POST'
		}).then(() => {
			console.log('DONE');
		});
	}

	function handleKeydown(event: KeyboardEvent) {
		if (event.key === 'c') {
			if (entries.length > 0) {
				console.log(`convert ${entries[0].path}`);
				doConvert(entries[0]);
				window.scrollTo({ top: 0, behavior: 'smooth' });
			}
		}
	}
</script>

<div tabindex="0" onkeydown={handleKeydown}>
	{#each entries as entry}
		<div class="entry" style={entry.visibility === 'private' ? 'color: gray;' : ''}>
			<div>
				<a href="/admin/entry/{entry.path}">Form</a> |
				<a href="/entry/{entry.path}">Show</a>
			</div>
			<div class="title">{entry.title}</div>
			<div class="side-by-side">
				<pre class="html">{entry.body}</pre>
				<pre class="mkdn">{html2md(entry.body)}</pre>
				<!-- eslint-disable-next-line svelte/no-at-html-tags -->
				<div class="rendered">{@html renderHTML(html2md(entry.body), {})}</div>
			</div>

			<div class="fix">
				<button onclick={(event) => handleConvert(event, entry)}>Convert!</button>
			</div>
		</div>
	{/each}
</div>

<style>
	.entry {
		border: 1px;
		margin: 8px;
	}

	.title {
		border-bottom: 3px solid darkblue;
	}

	pre {
		max-width: 600px;
		word-break: break-all;
	}

	button {
		padding: 8px;
		border: 1px solid black;
	}

	.side-by-side .html {
		float: left;
		width: 30%;
		word-break: break-all;
		overflow-x: scroll;
	}
	.side-by-side .mkdn {
		float: left;
		width: 30%;
		word-break: break-all;
		overflow-x: scroll;
	}
	.side-by-side .rendered {
		float: left;
		width: 30%;
		word-break: break-all;
		overflow-x: scroll;
	}

	.fix {
		clear: both;
	}
</style>

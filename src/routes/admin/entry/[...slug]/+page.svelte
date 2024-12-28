<script lang="ts">
	import type { Entry } from '$lib/db';
	import { error } from '@sveltejs/kit';
	import type { PageData } from './$types';
	import { debounce } from '$lib/utils';
	import { beforeNavigate } from '$app/navigation';
	import { type LinkPalletData } from '$lib/repository/AdminEntryRepository';
	import LinkPallet from '../../LinkPallet.svelte';

	import MarkdownEditor from '$lib/components/admin/MarkdownEditor.svelte';
	import { extractLinks } from '$lib/markdown';

	let { data }: { data: PageData } = $props();
	if (!data.entry) {
		error(500, 'Missing entry data');
	}

	let entry: Entry = data.entry;
	let title: string = $state(entry.title);
	let body: string = $state(entry.body); // 初期値として本文を保持
	let visibility: 'private' | 'public' = $state(entry.visibility);

	let original_title = entry.title;
	let original_body = entry.body;

	let isDirty = false;

	let currentLinks = extractLinks(entry.body);
	let linkPallet: LinkPalletData = $state(data.twohops);

	function loadLinks() {
		fetch(`/admin/api/entry/${entry.path}/links`, {
			method: 'GET'
		})
			.then((response) => {
				if (response.ok) {
					return response.json();
				} else {
					throw new Error('Failed to get total');
				}
			})
			.then((data) => {
				linkPallet = data;
			})
			.catch((error) => {
				console.error('Failed to get total:', error);
			});
	}

	let message = $state('');
	let messageType: 'success' | 'error' | '' = $state('');

	function clearMessage() {
		message = '';
		messageType = '';
	}

	function showMessage(type: 'success' | 'error', text: string) {
		messageType = type;
		message = text;
		setTimeout(() => {
			message = '';
			messageType = '';
		}, 5000); // Hide message after 5 seconds
	}

	async function handleDelete(event: Event) {
		event.preventDefault();

		const confirmed = confirm(`Are you sure you want to delete the entry "${title}"?`);
		if (confirmed) {
			clearMessage();

			const response = await fetch('/admin/api/entry/' + entry.path, {
				method: 'DELETE'
			});
			if (response.ok) {
				showMessage('success', 'Entry deleted successfully');
				location.href = '/admin';
			} else {
				showMessage('error', 'Failed to delete entry');
			}
		}
	}

	async function handleUpdateBody() {
		clearMessage();

		try {
			const request = {
				body,
				original_body
			};
			const response = await fetch('/admin/api/entry/' + entry.path + '/body', {
				method: 'POST',
				headers: {
					'Content-Type': 'application/json'
				},
				body: JSON.stringify(request)
			});
			if (response.ok) {
				showMessage('success', 'Entry updated successfully');
				isDirty = false; // Reset dirty flag on successful update
				original_body = body;
			} else {
				let errorDetails = 'Unknown error';
				try {
					const errorData = await response.json();
					if (errorData && errorData.error) {
						errorDetails = errorData.error;
					}
				} catch (e) {
					console.error('Failed to parse error response', e);
				}
				showMessage(
					'error',
					`Failed to update entry body: ${response.statusText} (${response.status}) - ${errorDetails}`
				);
			}
		} catch (e) {
			showMessage('error', 'Failed to update entry body');
			console.error('Failed to update entry body:', e);
		}
	}

	async function handleUpdateTitle() {
		message = '';
		messageType = '';

		try {
			const request = {
				title,
				original_title
			};
			const response = await fetch('/admin/api/entry/' + entry.path + '/title', {
				method: 'POST',
				headers: {
					'Content-Type': 'application/json'
				},
				body: JSON.stringify(request)
			});
			if (response.ok) {
				showMessage('success', 'Entry updated successfully');
				isDirty = false; // Reset dirty flag on successful update
				original_title = title;
			} else {
				let errorDetails = 'Unknown error';
				try {
					const errorData = await response.json();
					if (errorData && errorData.error) {
						errorDetails = errorData.error;
					}
				} catch (e) {
					console.error('Failed to parse error response', e);
				}
				showMessage(
					'error',
					`Failed to update entry title: ${response.statusText} (${response.status}) - ${errorDetails}`
				);
			}
		} catch (e) {
			showMessage('error', 'Failed to update entry title');
			console.error('Failed to update entry title:', e);
		}
	}

	async function createNewEntry(title: string): Promise<void> {
		clearMessage();

		try {
			const response = await fetch('/admin/api/entry', {
				method: 'POST',
				headers: {
					'Content-Type': 'application/json'
				},
				body: JSON.stringify({ title })
			});
			if (response.ok) {
				const data = await response.json();
				location.href = '/admin/entry/' + data.path;
			} else {
				showMessage('error', 'Failed to create new entry');
			}
		} catch (error) {
			console.error('Failed to create new entry:', error);
			showMessage(
				'error',
				`Failed to create new entry: ${error instanceof Error ? error.message : error}`
			);
		}
	}

	// デバウンスした自動保存関数
	const debouncedUpdateBody = debounce(() => {
		handleUpdateBody();
	}, 800);

	// title related.
	const debouncedTitleUpdate = debounce(() => {
		handleUpdateTitle();
	}, 500);

	function handleTitleInput() {
		isDirty = true;
		debouncedTitleUpdate();
	}

	// 入力イベントや変更イベントにデバウンスされた関数をバインド
	function handleInputBody() {
		isDirty = true;
		debouncedUpdateBody();

		const newLinks = extractLinks(body);
		if (currentLinks !== newLinks) {
			currentLinks = newLinks;
			loadLinks();
		}
	}

	function toggleVisibility() {
		const newVisibility = visibility === 'private' ? 'public' : 'private';
		fetch(`/admin/api/entry/${entry.path}/visibility`, {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json'
			},
			body: JSON.stringify({ visibility: newVisibility })
		})
			.then((response) => {
				if (response.ok) {
					return response.json();
				} else {
					throw new Error('Failed to update visibility');
				}
			})
			.then((data) => {
				visibility = data.visibility;
			})
			.catch((error) => {
				console.error('Failed to update visibility:', error);
				showMessage('error', `Failed to update visibility: ${error.message}`);
			});
	}

	beforeNavigate(({ cancel }) => {
		if (isDirty && !confirm('You have unsaved changes. Are you sure you want to leave?')) {
			cancel();
		}
	});
</script>

<div>
	<div class="container {entry.visibility === 'private' ? 'private' : ''}">
		<div class="left-pane">
			<form class="form">
				<div class="title-container">
					<input
						id="title"
						name="title"
						type="text"
						class="input"
						bind:value={title}
						oninput={handleTitleInput}
						required
					/>
					<button class="visibility-icon" onclick={toggleVisibility}
						>{visibility === 'private' ? '🔒️' : '🌍'}</button
					>
				</div>

				<div class="editor">
					<input type="hidden" name="body" bind:value={body} />
					<MarkdownEditor
						initialContent={body}
						onUpdateText={(content) => {
							body = content;
							handleInputBody(); // エディタ更新時にデバウンスされた更新をトリガー
						}}
						existsEntryByTitle={(title) => {
							return !!data.links[title.toLowerCase()];
						}}
						onClickEntry={(title) => {
							if (data.links[title.toLowerCase()]) {
								location.href = '/admin/entry/' + data.links[title.toLowerCase()];
							} else {
								createNewEntry(title);
							}
						}}
						onSave={() => {
							handleUpdateBody();
						}}
					></MarkdownEditor>
				</div>
			</form>
		</div>

		<div class="right-pane">
			<div class="button-container">
				<button type="submit" class="delete-button" onclick={handleDelete}> Delete </button>
			</div>

			<!-- link to the user side page -->
			{#if visibility === 'public'}
				<div class="link-container">
					<a href="/entry/{entry.path}" class="link">Go to User Side Page</a>
				</div>
			{/if}
		</div>
	</div>

	{#if message}
		<div class="popup-message {messageType}">
			<p>{message}</p>
		</div>
	{/if}

	<LinkPallet {linkPallet} />
</div>

<style>
	.container {
		display: flex;
		flex-wrap: wrap;
	}

	.container.private {
		background-color: #e5e7eb;
	}

	.left-pane {
		flex: 1;
		min-width: 300px;
		max-width: 800px;
	}

	.right-pane {
		margin-left: 1rem; /* Add some space between the panes */
		max-width: 300px;
	}

	.form {
		display: flex;
		flex-direction: column;
		gap: 1rem;
		padding: 1rem;
		max-width: 800px;
	}

	.input {
		width: calc(100% - 2rem); /* Adjust width to make space for the icon */
		border-radius: 0.375rem;
		border: 1px solid #d1d5db;
		padding: 0.5rem;
	}

	.title-container {
		display: flex;
		align-items: center;
	}

	.visibility-icon {
		border: 0;
		background-color: transparent;
		margin-left: 0.5rem;
		font-size: 1.5rem;
		cursor: pointer;
	}

	.editor {
		border: 1px solid #d1d5db;
		border-radius: 0.25rem;
		height: 400px;
		overflow-y: scroll;
	}

	.delete-button {
		border-radius: 0.375rem;
		background-color: #ef4444;
		padding: 0.5rem 1rem;
		color: white;
	}

	.delete-button:hover {
		background-color: #dc2626;
	}

	.link {
		border-radius: 0.375rem;
		background-color: #10b981;
		padding: 0.5rem 1rem;
		color: white;
		text-decoration: none;
	}

	.link:hover {
		text-decoration: underline;
	}

	.popup-message {
		position: fixed;
		bottom: 1rem;
		right: 1rem;
		padding: 1rem;
		border-radius: 0.375rem;
		box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
		z-index: 1000;
	}

	.popup-message.success {
		background-color: #10b981;
		color: white;
	}

	.popup-message.error {
		background-color: #ef4444;
		color: white;
	}

	@media (max-width: 600px) {
		.container {
			flex-direction: column;
		}

		.right-pane {
			margin-left: 0;
			margin-top: 1rem; /* Add some space between the panes */
		}
	}
</style>

<script lang="ts">
	import type { Entry } from '$lib/db';
	import { error } from '@sveltejs/kit';
	import type { PageData } from './$types';
	import { debounce } from '$lib/utils';
	import { beforeNavigate } from '$app/navigation';

	import MarkdownEditor from '$lib/components/admin/MarkdownEditor.svelte';
	import EntryList from '../../EntryList.svelte';
	import EntryCard from '../../EntryCard.svelte';

	let { data }: { data: PageData } = $props();
	if (!data.entry) {
		error(500, 'Missing entry data');
	}

	let entry: Entry = data.entry;
	let title: string = $state(entry.title);
	let body: string = $state(entry.body); // 初期値として本文を保持
	let visibility: 'private' | 'public' = $state(entry.visibility);

	let successMessage = $state('');
	let errorMessage = $state('');

	let isDirty = false;

	async function handleDelete(event: Event) {
		event.preventDefault();

		const confirmed = confirm(`Are you sure you want to delete the entry "${title}"?`);
		if (confirmed) {
			successMessage = '';
			errorMessage = '';

			const response = await fetch('/admin/api/entry/' + entry.path, {
				method: 'DELETE'
			});
			if (response.ok) {
				successMessage = 'Entry deleted successfully';
				location.href = '/admin';
			} else {
				errorMessage = 'Failed to delete entry';
			}
		}
	}

	async function handleUpdate() {
		successMessage = '';
		errorMessage = '';

		try {
			const request = {
				title,
				body,
				updated_at: entry.updated_at
			};
			const response = await fetch('/admin/api/entry/' + entry.path, {
				method: 'POST',
				headers: {
					'Content-Type': 'application/json'
				},
				body: JSON.stringify(request)
			});
			if (response.ok) {
				successMessage = 'Entry updated successfully';
				isDirty = false; // Reset dirty flag on successful update

				response.json().then((data) => {
					entry.updated_at = data.updated_at;
				});
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
				errorMessage = `Failed to update entry: ${response.statusText} (${response.status}) - ${errorDetails}`;
			}
		} catch (e) {
			errorMessage = 'Failed to update entry';
			console.error('Failed to update entry:', e);
		}
	}

	// デバウンスした自動保存関数
	const debouncedUpdate = debounce(() => {
		handleUpdate();
	}, 1000);

	// 入力イベントや変更イベントにデバウンスされた関数をバインド
	function handleInput() {
		isDirty = true;
		debouncedUpdate();
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
				errorMessage = `Failed to update visibility: ${error.message}`;
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
						oninput={handleInput}
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
							handleInput(); // エディタ更新時にデバウンスされた更新をトリガー
						}}
						existsEntryByTitle={(title) => {
							return !!data.links[title.toLowerCase()];
						}}
						onClickEntry={(title) => {
							if (data.links[title.toLowerCase()]) {
								location.href = '/admin/entry/' + data.links[title.toLowerCase()];
							} else {
								// create new entry by title
								fetch('/admin/api/entry', {
									method: 'POST',
									headers: {
										'Content-Type': 'application/json'
									},
									body: JSON.stringify({ title })
								})
									.then((response) => {
										if (response.ok) {
											return response.json();
										} else {
											throw new Error('Failed to create new entry');
										}
									})
									.then((data) => {
										location.href = '/admin/entry/' + data.path;
									})
									.catch((error) => {
										console.error('Failed to create new entry:', error);
										errorMessage = `Failed to create new entry: ${error.message}`;
									});
							}
						}}
						onSave={() => {
							handleUpdate();
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

	<div class="link-container">
		<div class="one-hop-link">
			Not found:
			{#each data.twohops.notFoundTitles as title}
				<div>{title}</div>
			{/each}
		</div>
		<div class="one-hop-link">
			Straight:
			{#each data.twohops.links as link}
				<EntryCard entry={link} />
			{/each}
		</div>
		{#each data.twohops.twohops as twohops}
			<div class="one-hop-link" style="border: 1px solid red; padding: 1rem; display: flex">
				<EntryCard entry={twohops.src} />
				{#each twohops.links as link}
					<EntryCard entry={link} />
				{/each}
			</div>
		{/each}
	</div>
</div>

{#if successMessage}
	<p class="success-message">{successMessage}</p>
{/if}

{#if errorMessage}
	<p class="error-message">{errorMessage}</p>
{/if}

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

	.success-message {
		color: #10b981;
	}

	.error-message {
		color: #ef4444;
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

	.one-hop-link {
		display: flex;
		flex-wrap: wrap;
		gap: 1rem;
		clear: both;
	}
</style>

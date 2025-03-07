<script lang="ts">
	import { type Entry } from '$lib/entity';
	import { error } from '@sveltejs/kit';
	import type { PageData } from './$types';
	import { debounce } from '$lib/utils';
	import { beforeNavigate } from '$app/navigation';
	import { type LinkPalletData } from '$lib/LinkPallet';
	import LinkPallet from '../../AdminLinkPallet.svelte';
	import { onMount } from 'svelte';

	import MarkdownEditor from '$lib/components/admin/MarkdownEditor.svelte';
	import { extractLinks } from '$lib/extractLinks';

	let { data }: { data: PageData } = $props();
	if (!data.entry) {
		error(500, 'Missing entry data');
	}

	let entry: Entry = data.entry;
	let title: string = $state(entry.title);
	let body: string = $state(entry.body); // 初期値として本文を保持
	let visibility: 'private' | 'public' = $state(entry.visibility);

	let pageTitles: string[] = $state([]);

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

	let updatedMessage = $state('');

	function showUpdatedMessage(text: string) {
		updatedMessage = text;
		setTimeout(() => (updatedMessage = ''), 1000);
	}

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
				body
			};
			const response = await fetch('/admin/api/entry/' + entry.path + '/body', {
				method: 'POST',
				headers: {
					'Content-Type': 'application/json'
				},
				body: JSON.stringify(request)
			});
			if (response.ok) {
				showUpdatedMessage('Updated');
				isDirty = false; // Reset dirty flag on successful update
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
				title
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

		if (!confirm(`Are you sure you want to change the visibility of this entry?`)) {
			return;
		}

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

	function getEditDistance(a: string, b: string): number {
		const dp = Array.from({ length: a.length + 1 }, () => Array(b.length + 1).fill(0));

		for (let i = 0; i <= a.length; i++) dp[i][0] = i;
		for (let j = 0; j <= b.length; j++) dp[0][j] = j;

		for (let i = 1; i <= a.length; i++) {
			for (let j = 1; j <= b.length; j++) {
				if (a[i - 1] === b[j - 1]) {
					dp[i][j] = dp[i - 1][j - 1];
				} else {
					dp[i][j] =
						Math.min(
							dp[i - 1][j], // 削除
							dp[i][j - 1], // 挿入
							dp[i - 1][j - 1] // 置換
						) + 1;
				}
			}
		}

		return dp[a.length][b.length];
	}

	async function getPageTitles(): Promise<string[]> {
		const resp = await fetch(`/admin/api/entry/title`, {
			method: 'GET'
		});
		const dat = await resp.json();
		return dat.titles;
	}

	// 定期的に本文情報を再取得する。
	// 他のユーザーが大幅に変更していた場合は警告を表示し、リロードを促す。
	// TODO: 編集不可状態とする。
	function checkOtherUsersUpdate() {
		fetch(`/admin/api/entry/${entry.path}`, {
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
				// 本文が短いときは消えてもダメージ少ないので無視
				if (data.body.length > 100 && !isDirty) {
					const threshold = Math.max(body.length, data.body.length) * 0.1; // 10%以上の変更で判定
					const editDistance = getEditDistance(body, data.body);
					if (editDistance > threshold) {
						if (
							confirm(
								`他のユーザーが大幅に変更しました。リロードしてください。 ${editDistance} > ${threshold}`
							)
						) {
							location.reload();
						}
					}
				}
			})
			.catch((error) => {
				console.error('Failed to get total:', error);
			});
	}

	onMount(() => {
		// get page titles
		setTimeout(async () => (pageTitles = await getPageTitles()), 0);

		document.addEventListener('visibilitychange', () => {
			checkOtherUsersUpdate();
		});
	});

	function selectIfPlaceholder(target: HTMLInputElement) {
		if (/^2\d+$/.test(target.value)) {
			target.select(); // 入力値を全選択
		}
	}
</script>

<div class="parent">
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
						onfocus={(event) => selectIfPlaceholder(event.target as HTMLInputElement)}
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
						{pageTitles}
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

			{#if updatedMessage !== ''}
				<div class="updated-message">
					{updatedMessage}
				</div>
			{/if}
		</div>
	</div>

	{#if message}
		<div class="popup-message {messageType}">
			<p>{message}</p>
		</div>
	{/if}

	<div class="link-pallet">
		<LinkPallet {linkPallet} />
	</div>
</div>

<style>
	.link-pallet {
		margin: auto;
		max-width: 1200px;
	}

	.container {
		display: flex;
		flex-wrap: wrap;
		max-width: 1200px;
		margin: auto;
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

	.updated-message {
		color: #10b981;
	}
</style>

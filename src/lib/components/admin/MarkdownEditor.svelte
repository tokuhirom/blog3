<script lang="ts">
	import { onMount } from 'svelte';
	import { EditorView, keymap } from '@codemirror/view';
	import { EditorState, Transaction } from '@codemirror/state';
	import { markdown, markdownLanguage } from '@codemirror/lang-markdown';
	import { history, historyKeymap, indentWithTab } from '@codemirror/commands';
	import { languages } from '@codemirror/language-data';
	import { oneDarkHighlightStyle } from '@codemirror/theme-one-dark';
	import { syntaxHighlighting } from '@codemirror/language';
	import { defaultKeymap } from '@codemirror/commands';

	let container: HTMLDivElement;
	export let initialContent: string = '';
	export let onUpdateText: (content: string) => void;
	export let onSave: (content: string) => void;
	let editor: EditorView;

	let isUploading: boolean = false; // アップロード中の状態
	let errorMessage: string = ''; // エラー通知メッセージ

	onMount(() => {
		const startState = EditorState.create({
			doc: initialContent,
			extensions: [
				markdown({
					base: markdownLanguage,
					codeLanguages: languages
				}),
				history(),
				syntaxHighlighting(oneDarkHighlightStyle),
				EditorView.lineWrapping,
				keymap.of([...historyKeymap, ...defaultKeymap, indentWithTab]),
				EditorView.theme({
					'.cm-editor': { height: '600px' },
					'.cm-content': { overflowY: 'auto' }
				}),
				EditorView.domEventHandlers({
					paste: handlePaste
				}),
				EditorView.updateListener.of((update) => {
					if (update.changes) {
						const isUserInput = update.transactions.some(
							(tr) => tr.annotation(Transaction.userEvent) !== 'program' && tr.docChanged
						);
						if (isUserInput && onUpdateText) {
							onUpdateText(editor.state.doc.toString());
						}
					}
				})
			]
		});

		editor = new EditorView({
			state: startState,
			parent: container
		});

		return () => {
			editor.destroy();
		};
	});

	function handlePaste(event: ClipboardEvent): void {
		const items = event.clipboardData?.items;
		if (!items) return;

		for (const item of items) {
			if (item.type.startsWith('image/')) {
				const file = item.getAsFile();
				if (file) {
					isUploading = true; // アップロード中フラグを立てる
					uploadImage(file)
						.then((url) => {
							insertMarkdownImage(url);
							event.preventDefault();
						})
						.catch((error) => {
							console.error('Image upload failed:', error);
							showError('Image upload failed. Please try again.');
						})
						.finally(() => {
							isUploading = false; // アップロード完了でフラグを下ろす
						});
				}
			} else if (item.type === 'text/plain') {
				const text = event.clipboardData.getData('text/plain');
				insertTextAtCursor(text);
				event.preventDefault();
			}
		}
	}

	function insertTextAtCursor(text: string) {
		const transaction = editor.state.update({
			changes: { from: editor.state.selection.main.from, insert: text }
		});
		editor.dispatch(transaction);
	}

	async function uploadImage(file: File): Promise<string> {
		const formData = new FormData();
		formData.append('file', file);

		const response = await fetch('/admin/api/upload', {
			method: 'POST',
			body: formData
		});

		if (!response.ok) {
			throw new Error('Failed to upload image');
		}

		const data = await response.json();
		return data.url;
	}

	function insertMarkdownImage(url: string) {
		const markdownImage = `![Image](${url})`;
		const transaction = editor.state.update({
			changes: {
				from: editor.state.selection.main.from,
				insert: markdownImage
			}
		});
		editor.dispatch(transaction);
	}

	function showError(message: string) {
		errorMessage = message;
		setTimeout(() => {
			errorMessage = ''; // 7秒後にエラーメッセージを非表示
		}, 7000);
	}

	const handleKeyDown = (event: KeyboardEvent) => {
		if ((event.ctrlKey || event.metaKey) && event.key === 's') {
			event.preventDefault();
			onSave(editor.state.doc.toString());
		}
	};
</script>

<svelte:window on:keydown={handleKeyDown} />

<div class="wrapper">
	<div bind:this={container}></div>
	{#if isUploading}
		<div class="upload-indicator">Uploading image...</div>
	{/if}
	{#if errorMessage}
		<div class="error-indicator">{errorMessage}</div>
	{/if}
</div>

<style>
	.wrapper {
		width: 100%;
		height: 100%;
	}

	.upload-indicator {
		position: fixed;
		bottom: 20px;
		right: 20px;
		background: rgba(0, 0, 0, 0.7);
		color: white;
		padding: 10px 15px;
		border-radius: 5px;
		font-size: 14px;
	}

	.error-indicator {
		position: fixed;
		bottom: 60px;
		right: 20px;
		background: rgba(255, 0, 0, 0.8);
		color: white;
		padding: 10px 15px;
		border-radius: 5px;
		font-size: 14px;
	}
</style>

<script lang="ts">
	import { onMount } from 'svelte';
	import { EditorView, keymap } from '@codemirror/view';
	import { EditorState, Transaction } from '@codemirror/state';
	import { markdown, markdownLanguage } from '@codemirror/lang-markdown';
	import { history, historyKeymap } from '@codemirror/commands';
	import { languages } from '@codemirror/language-data';
	import { oneDarkHighlightStyle } from '@codemirror/theme-one-dark';
	import { syntaxHighlighting } from '@codemirror/language';
	import { defaultKeymap } from '@codemirror/commands';

	let container: HTMLDivElement; // エディタの親要素
	export let initialContent: string = ''; // 初期コンテンツ
	export let onUpdateText: (content: string) => void; // 更新時のコールバック
	export let onSave: (content: string) => void;
	let editor: EditorView;

	onMount(() => {
		const startState = EditorState.create({
			doc: initialContent,
			extensions: [
				markdown({
					base: markdownLanguage, // Markdown サポート
					codeLanguages: languages // コードブロックの言語
				}),
				history(),
				syntaxHighlighting(oneDarkHighlightStyle), // ダークテーマのシンタックスハイライト
				EditorView.lineWrapping, // 行の折り返し
				keymap.of([...historyKeymap, ...defaultKeymap]),
				EditorView.theme({
					// 高さ固定のテーマ
					'.cm-editor': { height: '600px' },
					'.cm-content': { overflowY: 'auto' }
				}),
				EditorView.domEventHandlers({
					paste: handlePaste
				}),
				EditorView.updateListener.of((update) => {
					if (update.changes) {
						// ユーザーの入力変更を反映
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
			editor.destroy(); // コンポーネントが破棄されたときにエディタをクリーンアップ
		};
	});

	function handlePaste(event: ClipboardEvent): void {
		const items = event.clipboardData?.items;
		if (!items) return;

		for (const item of items) {
			if (item.type.startsWith('image/')) {
				const file = item.getAsFile();
				if (file) {
					uploadImage(file)
						.then((url) => {
							insertMarkdownImage(url);
							insertMarkdownImage(url);
							event.preventDefault();
						})
						.catch((error) => {
							console.error('Image upload failed:', error);
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
</div>

<style>
	.wrapper {
		width: 100%;
		height: 100%;
	}
</style>

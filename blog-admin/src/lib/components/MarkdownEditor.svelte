<script lang="ts">
	import { onMount } from 'svelte';
	import { EditorView, keymap } from '@codemirror/view';
	import { EditorState, type Extension, Transaction } from '@codemirror/state';
	import { markdown, markdownLanguage } from '@codemirror/lang-markdown';
	import { languages } from '@codemirror/language-data';
	import { oneDarkHighlightStyle } from '@codemirror/theme-one-dark';
	import { syntaxHighlighting } from '@codemirror/language';
	import { defaultKeymap } from '@codemirror/commands';

	let container; // エディタの親要素
	export let initialContent: string = ''; // 初期コンテンツ
	export let onUpdateText: (content: string) => void; // 更新時のコールバック
	let editor: EditorView;

	onMount(() => {
		const startState = EditorState.create({
			doc: initialContent,
			extensions: [
				markdown({
					base: markdownLanguage, // Markdown サポート
					codeLanguages: languages // コードブロックの言語
				}),
				syntaxHighlighting(oneDarkHighlightStyle), // ダークテーマのシンタックスハイライト
				EditorView.lineWrapping, // 行の折り返し
				keymap.of(defaultKeymap), // デフォルトのキーバインド
				EditorView.theme({
					// 高さ固定のテーマ
					'.cm-editor': { height: '600px' },
					'.cm-content': { overflowY: 'auto' }
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
</script>

<div class="wrapper">
	<div bind:this={container}></div>
</div>

<style>
	.wrapper {
		width: 100%;
		height: 100%;
	}
</style>

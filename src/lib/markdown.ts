import MarkdownIt from 'markdown-it';
import type { Entry } from './db';
import hljs from 'highlight.js'; // highlight.js をインポート

const md: MarkdownIt = new MarkdownIt({
	linkify: true, // URL を自動リンク化
	highlight: (code, lang) => {
		if (lang && hljs.getLanguage(lang)) {
			// 指定されたシンタックスでハイライト
			try {
				return `<pre class="hljs"><code>${hljs.highlight(code, { language: lang }).value}</code></pre>`;
			} catch {
				// 無視
			}
		}
		// シンタックス指定がない場合、そのままエスケープして表示
		return `<pre class="hljs"><code>${md.utils.escapeHtml(code)}</code></pre>`;
	}
});

export function renderHTML(markdown: string): string {
	return md.render(markdown);
}

export function renderHTMLByEntry(entry: Entry): string {
	if (entry.format === 'mkdn') {
		return renderHTML(entry.body);
	} else {
		console.log('Unsupported format: ', entry.format);
		return entry.body;
	}
}

import MarkdownIt from 'markdown-it';
import { type Entry } from '$lib/entity';
import hljs from 'highlight.js'; // highlight.js をインポート

// カスタム記法用のプラグインを定義
function buildEntryLinkPlugin(links: { [key: string]: string | null }): (md: MarkdownIt) => void {
	return function entryLinkPlugin(md: MarkdownIt) {
		md.inline.ruler.after('link', 'entry_link', (state, silent) => {
			const start = state.pos;
			const max = state.posMax;

			// [[ の検出
			if (
				state.src.charCodeAt(start) !== 0x5b /* [ */ ||
				state.src.charCodeAt(start + 1) !== 0x5b /* [ */
			) {
				return false;
			}

			let end = start + 2;
			while (end < max) {
				if (
					state.src.charCodeAt(end) === 0x5d /* ] */ &&
					state.src.charCodeAt(end + 1) === 0x5d /* ] */
				) {
					break;
				}
				end++;
			}

			// ]] が見つからない場合は無視
			if (end >= max) {
				return false;
			}

			// パースする場合
			if (!silent) {
				const content = state.src.slice(start + 2, end);
				const token = state.push('entry_link', 'a', 0);
				token.attrs = [
					['class', 'entry-link'],
					['data-title', content]
				];
				token.content = content;
				token.markup = '[[';
				token.info = '';
			}

			state.pos = end + 2; // 次の位置に進める
			return true;
		});

		md.renderer.rules.entry_link = (tokens, idx) => {
			const token = tokens[idx];
			const title = token.content;
			const pageFound = links[token.content.toLowerCase()];
			return `<a ${pageFound ? 'href="/entry/' + pageFound + '"' : ''} class="entry-link ${pageFound ? 'found' : 'not-found'}" data-title="${md.utils.escapeHtml(title)}">${md.utils.escapeHtml(title)}</a>`;
		};
	};
}

export function renderHTML(markdown: string, links: { [key: string]: string | null }): string {
	const md: MarkdownIt = new MarkdownIt({
		// Enable HTML tags in source
		html: true,
		// Autoconvert URL-like text to links
		linkify: true,
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
	md.core.ruler.push('ignore_html_comments', (state) => {
		state.tokens = state.tokens.filter((token) => {
			if (token.type === 'html_block' || token.type === 'html_inline') {
				return !token.content.trim().startsWith('<!--');
			}
			return true;
		});
	});
	md.use(buildEntryLinkPlugin(links));

	return md.render(markdown);
}

export function renderHTMLByEntry(entry: Entry, links: { [key: string]: string | null }): string {
	if (entry.format === 'mkdn') {
		return renderHTML(entry.body, links);
	} else if (entry.format === 'html') {
		return entry.body;
	} else {
		throw new Error(`Unknown format: ${entry.format}`);
	}
}

export function extractLinks(markdown: string): string[] {
	const links: string[] = markdown.match(/\[\[(.+?)\]\]/g) || [];
	const seen = new Set();
	const result: string[] = [];
	for (const link of links) {
		if (!seen.has(link.toLowerCase())) {
			seen.add(link.toLowerCase());
			result.push(link.slice(2, -2));
		}
	}
	return result;
}

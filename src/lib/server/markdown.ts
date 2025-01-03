import MarkdownIt from 'markdown-it';
import type { Entry } from '$lib/entity';
import hljs from 'highlight.js'; // highlight.js をインポート
import { getProductDetails } from '$lib/server/repository/AmazonRepository';

async function preloadAsinCache(markdown: string): Promise<Record<string, string | null>> {
	const matches = markdown.match(/\[asin:(.+?):detail\]/g) || [];
	const uniqueAsins = [...new Set(matches.map((match) => match.slice(6, -8)))];
	const asinCache: Record<string, string | null> = {};

	for (const asin of uniqueAsins) {
		console.log(`getting amazon product info: ${asin}`);
		if (!(asin in asinCache)) {
			try {
				const product = await getProductDetails(asin);
				if (product) {
					asinCache[asin] = `
                        <div class="asin-product">
                            <a href="${product.link}" target="_blank">
                                <img src="${product.image_medium_url}" alt="${product.title}" />
                            </a>
                            <p><a href="${product.link}" target="_blank">${product.title}</a></p>
                        </div>
                    `;
				} else {
					asinCache[asin] = null; // キャッシュに "見つからなかった" 状態を保存
				}
			} catch (error) {
				console.error(error instanceof Error ? error.message : error);
				asinCache[asin] = null; // エラー発生時もキャッシュに null を保存
			}
		}
	}
	return asinCache;
}

// Amazon ASINリンクを処理するカスタムプラグインを定義
function buildAsinLinkPlugin(asinCache: Record<string, string | null>): (md: MarkdownIt) => void {
	return function asinLinkPlugin(md: MarkdownIt) {
		md.inline.ruler.after('link', 'asin_link', (state, silent) => {
			const start = state.pos;
			const max = state.posMax;

			// [asin: の検出
			if (
				state.src.slice(start, start + 6) !== '[asin:' ||
				!state.src.includes(':detail]', start)
			) {
				return false;
			}

			const end = state.src.indexOf(':detail]', start);
			if (end === -1 || end >= max) {
				return false;
			}

			// パースする場合
			if (!silent) {
				const asin = state.src.slice(start + 6, end);
				const token = state.push('asin_link', 'span', 0);
				token.attrs = [['class', 'asin-link']];
				token.content = asin;
				token.markup = '[asin:';
				token.info = ':detail]';
			}

			state.pos = end + 8; // 次の位置に進める
			return true;
		});

		md.renderer.rules.asin_link = (tokens, idx) => {
			const token = tokens[idx];
			const asin = token.content;

			// Amazon APIから商品情報を取得
			const html = asinCache[asin];
			if (html) {
				return html;
			} else {
				return `<a href="https://www.amazon.co.jp/dp/${asin}?tag=tokuhirom-22">Amazon</a>`;
			}
		};
	};
}

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

export async function renderHTML(
	markdown: string,
	links: { [key: string]: string | null }
): string {
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
	const cache = await preloadAsinCache(markdown);
	console.log(cache);
	md.use(buildAsinLinkPlugin(cache));

	return md.render(markdown);
}

export async function renderHTMLByEntry(
	entry: Entry,
	links: { [key: string]: string | null }
): string {
	if (entry.format === 'mkdn') {
		return await renderHTML(entry.body, links);
	} else if (entry.format === 'html') {
		return entry.body;
	} else {
		throw new Error(`Unknown format: ${entry.format}`);
	}
}

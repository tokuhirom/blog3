import type { RequestHandler } from '@sveltejs/kit';
import { create } from 'xmlbuilder2';
import { PublicEntryRepository } from '$lib/server/repository/PublicEntryRepository';
import { renderHTMLByEntry } from '$lib/server/markdown';
import { convert } from 'html-to-text';

function convertJstToUtc(jstDatetime: string): string {
	// jsDateTime is comming from mysql's DATETIME column.
	const jstDate = new Date(`${jstDatetime} GMT+0900`);
	return jstDate.toUTCString();
}

export const GET: RequestHandler = async () => {
	const { entries } = await PublicEntryRepository.getPaginatedEntry(1, 30);

	// RSSフィードのヘッダーを作成
	const feed = create({ version: '1.0' })
		.ele('rss', {
			version: '2.0',
			'xmlns:content': 'http://purl.org/rss/1.0/modules/content/',
			'xmlns:atom': 'http://www.w3.org/2005/Atom'
		})
		.ele('channel')
		.ele('title')
		.txt("tokuhirom's blog")
		.up()
		.ele('link')
		.txt('https://blog.64p.org')
		.up()
		.ele('atom:link')
		.att('href', 'https://blog.64p.org/feed')
		.att('rel', 'self')
		.att('type', 'application/rss+xml')
		.up()
		.ele('description')
		.txt('The latest articles from my blog')
		.up()
		.ele('language')
		.txt('en')
		.up()
		.ele('pubDate')
		.txt(new Date().toUTCString())
		.up();

	// エントリを追加
	for (const entry of entries) {
		const html = await renderHTMLByEntry(entry, {});
		const text = convert(html, {
			wordwrap: 80 // テキストの行幅を設定
		});

		feed
			.ele('item')
			.ele('title')
			.txt(entry.title)
			.up()
			.ele('link')
			.txt(`https://blog.64p.org/entry/${entry.path}`)
			.up()
			.ele('description')
			.txt(text)
			.up()
			.ele('content:encoded')
			.dat(html)
			.up()
			.ele('pubDate')
			.txt(convertJstToUtc(entry.published_at!))
			.up()
			.ele('guid')
			.txt(`https://blog.64p.org/entry/${entry.path}`)
			.up()
			.up();
	}

	// XML文字列を生成
	const xml = feed.end({ prettyPrint: true });

	return new Response(xml, {
		headers: {
			'Content-Type': 'application/xml; charset=utf-8'
		}
	});
};

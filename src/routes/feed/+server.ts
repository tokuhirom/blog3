import type { RequestHandler } from '@sveltejs/kit';
import { create } from 'xmlbuilder2';
import { PublicEntryRepository } from '$lib/repository/PublicEntryRepository';
import { renderHTMLByEntry } from '$lib/markdown';

export const GET: RequestHandler = async () => {
	const { entries } = await PublicEntryRepository.getPaginatedEntry(1, 30);

	// RSSフィードのヘッダーを作成
	const feed = create({ version: '1.0' })
		.ele('rss', { version: '2.0', 'xmlns:content': 'http://purl.org/rss/1.0/modules/content/' })
		.ele('channel')
		.ele('title')
		.txt('My Blog RSS Feed')
		.up()
		.ele('link')
		.txt('https://blog.64p.org')
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
	entries.forEach((entry) => {
		feed
			.ele('item')
			.ele('title')
			.txt(entry.title)
			.up()
			.ele('link')
			.txt(`https://blog.64p.org/entry/${entry.path}`)
			.up()
			.ele('description')
			.txt(entry.body)
			.up()
			.ele('content:encoded')
			.dat(renderHTMLByEntry(entry, {}))
			.up()
			.ele('pubDate')
			.txt(new Date(entry.published_at!).toUTCString())
			.up()
			.ele('guid')
			.txt(`https://blog.64p.org/entry/${entry.path}`)
			.up()
			.up();
	});

	// XML文字列を生成
	const xml = feed.end({ prettyPrint: true });

	return new Response(xml, {
		headers: {
			'Content-Type': 'application/xml; charset=utf-8'
		}
	});
};

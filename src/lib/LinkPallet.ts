import type { Entry } from './entity';
import type { HasDestTitle, TwoHopLink } from './server/repository/AdminEntryRepository';

export type LinkPalletData = {
	newLinks: string[];
	links: Entry[];
	twohops: TwoHopLink[];
};

export function buildLinkPalletData(
	links: (HasDestTitle & Entry)[],
	reverseLinks: Entry[],
	twohopEntries: (HasDestTitle & Entry)[],
	targetPath: string
): LinkPalletData {
	// twohopEntries を dst_title でグループ化
	const twohopEntriesByTitle: { [key: string]: Entry[] } = {};
	for (const entry of twohopEntries) {
		if (!twohopEntriesByTitle[entry.dst_title.toLowerCase()]) {
			twohopEntriesByTitle[entry.dst_title.toLowerCase()] = [];
		}
		twohopEntriesByTitle[entry.dst_title.toLowerCase()].push(entry);
	}

	const resultLinks: Entry[] = [];
	const resultTwoHops: TwoHopLink[] = [];
	const newLinks: string[] = [];
	const seenPath = new Set([targetPath]);

	// twohopEntries に入っているエントリのリストを作成
	for (const link of links) {
		if (link.path) {
			seenPath.add(link.path);
		}

		if (twohopEntriesByTitle[link.dst_title.toLowerCase()]) {
			resultTwoHops.push({
				src: link,
				links: twohopEntriesByTitle[link.dst_title.toLowerCase()]
			});
			for (const entry of twohopEntriesByTitle[link.dst_title.toLowerCase()]) {
				seenPath.add(entry.path);
			}
		} else {
			if (link.body) {
				resultLinks.push(link);
			} else {
				newLinks.push(link.dst_title);
			}
		}
	}
	for (const reverseLink of reverseLinks) {
		if (!seenPath.has(reverseLink.path)) {
			resultLinks.push(reverseLink);
		}
	}

	return {
		newLinks: Array.from(new Set(newLinks)),
		links: Array.from(new Set(resultLinks)),
		twohops: resultTwoHops
	};
}

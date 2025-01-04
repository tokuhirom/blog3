import { buildLinkPalletData, type LinkPalletData } from '$lib/LinkPallet';
import type { PublicEntryRepository } from '../repository/PublicEntryRepository';

export class PublicEntryService {
	publicEntryRepository: PublicEntryRepository;

	constructor(publicEntryRepository: PublicEntryRepository) {
		this.publicEntryRepository = publicEntryRepository;
	}

	/**
	 * Get two hop links from the entry.
	 */
	async getLinkPalletData(targetPath: string, targetTitle: string): Promise<LinkPalletData> {
		if (!targetTitle) {
			throw new Error('Missing targetTitle');
		}

		// このエントリがリンクしているページのリストを取得
		const links = await this.publicEntryRepository.getLinkedEntries(targetPath);
		console.log(
			'links:',
			links.map((link) => link.dst_title)
		);
		// このエントリにリンクしているページのリストを取得
		const reverseLinks = await this.publicEntryRepository.getEntriesByLinkedTitle(targetTitle);
		console.log(
			'reverseLinks:',
			reverseLinks.map((link) => link.title)
		);
		// links の指す先のタイトルにリンクしているエントリのリストを取得
		const twohopEntries = await this.publicEntryRepository.getEntriesByLinkedTitles(
			targetPath,
			links.map((link) => link.dst_title.toLowerCase())
		);
		console.log(
			'twohopEntries:',
			twohopEntries.map((link) => 'dest=' + link.dst_title + ' ' + link.title)
		);

		return buildLinkPalletData(links, reverseLinks, twohopEntries, targetPath);
	}
}

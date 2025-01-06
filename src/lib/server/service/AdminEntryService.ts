import { HUB_URLS } from '$lib/config';
import { buildLinkPalletData, type LinkPalletData } from '$lib/LinkPallet';
import { notifyHub } from '../pubsubhubbub';
import type { AdminEntryRepository } from '../repository/AdminEntryRepository';

export class AdminEntryService {
	adminEntryRepository: AdminEntryRepository;

	constructor(adminEntryRepository: AdminEntryRepository) {
		this.adminEntryRepository = adminEntryRepository;
	}

	getTitles() {
		return this.adminEntryRepository.getTitles();
	}

	/**
	 * Get two hop links from the entry.
	 */
	async getLinkPalletData(targetPath: string, targetTitle: string): Promise<LinkPalletData> {
		if (!targetTitle) {
			throw new Error('Missing targetTitle');
		}

		// このエントリがリンクしているページのリストを取得
		const links = await this.adminEntryRepository.getLinkedEntries(targetPath);
		console.log(
			'links:',
			links.map((link) => link.dst_title)
		);
		// このエントリにリンクしているページのリストを取得
		const reverseLinks = await this.adminEntryRepository.getEntriesByLinkedTitle(targetTitle);
		console.log(
			'reverseLinks:',
			reverseLinks.map((link) => link.title)
		);
		// links の指す先のタイトルにリンクしているエントリのリストを取得
		const twohopEntries = await this.adminEntryRepository.getEntriesByLinkedTitles(
			targetPath,
			links.map((link) => link.dst_title.toLowerCase())
		);
		console.log(
			'twohopEntries:',
			twohopEntries.map((link) => 'dest=' + link.dst_title + ' ' + link.title)
		);

		return buildLinkPalletData(links, reverseLinks, twohopEntries, targetPath);
	}

	async updateEntryVisibility(path: string, newVisibility: 'private' | 'public'): Promise<void> {
		await this.adminEntryRepository.updateEntryVisibility(path, newVisibility);
		for (const hubUrl of HUB_URLS) {
			console.log(`Notify Hub: ${hubUrl}`);
			await notifyHub(hubUrl, `https://blog.64p.org/feed`);
		}
	}
}

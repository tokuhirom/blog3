import type { Entry } from '$lib/entity';
import { db } from '$lib/server/db';
import type { EntryImageRepository } from '$lib/server/repository/EntryImageRepository';
import type { AmazonRepository } from '$lib/server/repository/AmazonRepository';

export class EntryImageService {
	entryImageRepository: EntryImageRepository;
	amazonRepository: AmazonRepository;

	constructor(entryImageRepository: EntryImageRepository, amazonRepository: AmazonRepository) {
		this.entryImageRepository = entryImageRepository;
		this.amazonRepository = amazonRepository;
	}

	async processEntryImages() {
		// get not processed entries
		const entries = await this.entryImageRepository.getNotProcessedEntries();
		for (const entry of entries) {
			// process entry
			await this.processEntry(entry);
		}
	}

	async processEntry(entry: Entry) {
		console.log(`process entry image: ${entry.path}`);

		// get images from entry body
		const image = await this.getImageFromEntry(entry);

		// insert image
		if (image === null && entry.body.match(/\[asin:/)) {
			// if image is not available, skip it.
			// maybe, ASIN processing is delayed.
			return;
		} else {
			await this.entryImageRepository.insertEntryImage(entry.path, image);
		}
	}

	async insertEntryImage(path: string, image: Promise<string | null>) {
		await db.query(
			`
            INSERT INTO entry_image (path, url)
            VALUES (?, ?)
            `,
			[path, image]
		);
	}

	async getImageFromEntry(entry: Entry): Promise<string | null> {
		// extract image url from entry body.

		// image pattern is following:
		// ![Image](https://blog-attachments.64p.org/1735866754875-image.png)
		// [![Image from Gyazo](https://i.gyazo.com/d58c72d37ca373ab293184cdb5e6e6bb.jpg)](https://gyazo.com/d58c72d37ca373ab293184cdb5e6e6bb)
		// [asin:4022520221:detail]
		// <img src="https://blog-attachments.64p.org/20240318-08591291046a4b-d1c4-4282-8998-fba07edb19a6.png" style="width:100%">
		//
		// Note, ASIN pattern is not just a URL. So, get the concrete image URL from the database.
		// if it's not available yet, skip it.

		const imageTagMatch = entry.body.match(
			/<img[^>]*src=['"]?(https?:\/\/[^\s)]+)\.(?:jpg|png|gif)['"]?/
		);
		if (imageTagMatch) {
			console.log(`imageTagMatch: ${imageTagMatch[1]}`);
			return imageTagMatch[1];
		}

		const basicImage = entry.body.match(/!\[.*?\]\((https?:\/\/[^\s)]+)\)/);
		if (basicImage) {
			console.log(`basicImage: ${basicImage[1]}`);
			return basicImage[1];
		}

		const gyazoImage = entry.body.match(/\[!\[.*?\]\((https?:\/\/[^\s)]+)\)\]\((.*?)\)/);
		if (gyazoImage) {
			console.log(`gyazoImage: ${gyazoImage[1]}`);
			return gyazoImage[1];
		}

		const asin = entry.body.match(/\[asin:([A-Z0-9]+):detail\]/i);
		if (asin) {
			const imageUrl = await this.amazonRepository.getAsinImage(asin[1]);
			console.log(`asin: asin=${asin[1]} imageUrl=${imageUrl}`);
			return imageUrl;
		}

		return null;
	}
}

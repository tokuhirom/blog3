import { AmazonRepository } from '../repository/AmazonRepository';
import { EntryImageRepository } from '../repository/EntryImageRepository';
import { EntryImageService } from '../service/EntryImageService';

async function doProcessEntryImage() {
	const entryImageRepository = new EntryImageRepository();
	const amazonRepository = new AmazonRepository();
	const entryImageService = new EntryImageService(entryImageRepository, amazonRepository);
	await entryImageService.processEntryImages();
}

export function startEntryImageWorker() {
	console.log('Starting EntryImageWorker...');
	setTimeout(doProcessEntryImage, 1000);
	setInterval(doProcessEntryImage, 1000 * 60 * 60);
}

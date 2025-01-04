import type { AdminEntryRepository } from '../repository/AdminEntryRepository';

export class AdminEntryService {
	adminEntryRepository: AdminEntryRepository;

	constructor(adminEntryRepository: AdminEntryRepository) {
		this.adminEntryRepository = adminEntryRepository;
	}

	getTitles() {
		return this.adminEntryRepository.getTitles();
	}
}

// See https://svelte.dev/docs/kit/types#app.d.ts
// for information about these interfaces

import { AdminEntryRepository } from '$lib/server/repository/AdminEntryRepository';
import { AdminEntryService } from '$lib/server/service/AdminEntryService';

declare global {
	namespace App {
		// interface Error {}
		interface Locals {
			adminEntryRepository: AdminEntryRepository;
			adminEntryService: AdminEntryService;
		}
		// interface PageData {}
		// interface PageState {}
		// interface Platform {}
	}
}

export {};

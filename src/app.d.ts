// See https://svelte.dev/docs/kit/types#app.d.ts
// for information about these interfaces

import { AdminEntryRepository } from '$lib/db';

declare global {
	namespace App {
		// interface Error {}
		interface Locals {
			adminEntryRepository: AdminEntryRepository;
		}
		// interface PageData {}
		// interface PageState {}
		// interface Platform {}
	}
}

export {};

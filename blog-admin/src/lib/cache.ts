import { EntryModel, type Entry } from "./db";

let cache: Entry[] | null = null;

export class EntryCache {
    static async get() : Promise<Entry[]> {
        if (cache == null) {
            console.log("Cache miss");
            const got = await EntryModel.getAllEntries();
            cache = got;
            return got;
        } else {
            console.log("Cache hit");
            return cache;
        }
    }

    static clear() {
        cache = null;
    }
}

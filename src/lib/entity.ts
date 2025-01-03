export type Entry = {
	path: string;
	title: string;
	body: string;
	visibility: 'private' | 'public';
	format: 'html' | 'mkdn';
	created_at: string;
	updated_at: string | null;
	published_at: string | null;
};

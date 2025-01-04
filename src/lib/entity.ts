export type Entry = {
	path: string;
	title: string;
	body: string;
	visibility: 'private' | 'public';
	format: 'html' | 'mkdn';
	created_at: string;
	updated_at: string | null;
	published_at: string | null;
	last_edited_at: string;
};

export type AmazonCache = {
	asin: string;
	title: string | null;
	image_medium_url: string | null;
	link: string;
};

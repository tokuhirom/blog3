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

export type AmazonCache = {
	asin: string;
	title: string | null;
	image_medium_url: string | null;
	link: string;
};

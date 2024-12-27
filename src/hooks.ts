import type { Reroute } from '@sveltejs/kit';

const translated: Record<string, string> = {
	'//feed': '/feed'
};

export const reroute: Reroute = ({ url }) => {
	console.log(url.pathname);
	if (url.pathname in translated) {
		return translated[url.pathname];
	}
};

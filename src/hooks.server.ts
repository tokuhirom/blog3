import type { Handle } from '@sveltejs/kit';
import { AdminEntryRepository } from '$lib/repository/AdminEntryRepository';

if (!process.env.BASIC_AUTH_USERNAME || !process.env.BASIC_AUTH_PASSWORD) {
	console.error(
		'FATAL: Missing required BASIC_AUTH_USERNAME or BASIC_AUTH_PASSWORD environment variables.'
	);
	process.exit(1);
}

const BASIC_AUTH_USERNAME = process.env.BASIC_AUTH_USERNAME;
const BASIC_AUTH_PASSWORD = process.env.BASIC_AUTH_PASSWORD;

export const handle: Handle = async ({ event, resolve }) => {
	const path = event.url.pathname;
	const request = event.request;

	const forbidden =
		(request.method === 'POST' ||
			request.method === 'PUT' ||
			request.method === 'PATCH' ||
			request.method === 'DELETE') &&
		request.headers.get('origin') !== event.url.origin;

	if (forbidden) {
		console.error(
			`CSRF detected: method=${request.method} path=${path} origin=${event.url.origin} request.headers.origin=${request.headers.get('origin')}`
		);
		return new Response('Forbidden', {
			status: 403,
			headers: {
				'X-CSRF-Detected': 'true'
			}
		});
	}

	if (path.startsWith('/admin')) {
		const authHeader = event.request.headers.get('authorization');

		if (!authHeader || !authHeader.startsWith('Basic ')) {
			return new Response('Unauthorized', {
				status: 401,
				headers: {
					'WWW-Authenticate': 'Basic realm="Admin Area"'
				}
			});
		}

		const base64Credentials = authHeader.split(' ')[1];
		const credentials = atob(base64Credentials).split(':');
		const [username, password] = credentials;

		if (username !== BASIC_AUTH_USERNAME || password !== BASIC_AUTH_PASSWORD) {
			return new Response('Unauthorized', {
				status: 401,
				headers: {
					'WWW-Authenticate': 'Basic realm="Admin Area"'
				}
			});
		}

		event.locals.adminEntryRepository = new AdminEntryRepository();
		return resolve(event);
	} else {
		return resolve(event);
	}
};

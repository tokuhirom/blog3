import type { Handle } from '@sveltejs/kit';
import { AdminEntryRepository } from '$lib/repository/AdminEntryRepository';

// 必須環境変数をチェック
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

	// event.url.origin が `http://blog.64p.org` のように http**s** にならないので
	// ORIGIN 環境変数を使えるようにする
	const origin = process.env.ORIGIN || event.url.origin;
	const forbidden =
		(request.method === 'POST' ||
			request.method === 'PUT' ||
			request.method === 'PATCH' ||
			request.method === 'DELETE') &&
		request.headers.get('origin') !== origin;

	if (forbidden) {
		console.error(
			`CSRF detected: method=${request.method} path=${path} origin=${origin} event.url.origin=${event.url.origin} request.headers.origin=${request.headers.get('origin')}`
		);
		return new Response('Forbidden', {
			status: 403,
			headers: {
				'X-CSRF-Detected': 'true'
			}
		});
	}

	// `/admin` 以下のパスにのみ認証を適用
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

		// 認証成功時、通常の処理を続行
		event.locals.adminEntryRepository = new AdminEntryRepository();
		return resolve(event);
	} else {
		// It's not an admin page. Continue as normal.
		return resolve(event);
	}
};

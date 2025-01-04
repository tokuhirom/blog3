// https://amzn.to/42051PN のような記法を B01M2BOZDL に変換する
export async function amazonShortUrlToAsin(url: string): Promise<string> {
	try {
		// Fetch the short URL with `redirect: "manual"` to avoid following the redirect
		const response = await fetch(url, { method: 'GET', redirect: 'manual' });

		// Check if the Location header exists
		const location = response.headers.get('Location');
		if (!location) {
			throw new Error('Location header not found in the response');
		}

		// Extract the ASIN from the URL using a regular expression
		const asinMatch = location.match(/\/dp\/([A-Z0-9]{10})/);
		if (!asinMatch) {
			throw new Error('ASIN not found in the Location URL');
		}

		// Return the extracted ASIN
		return asinMatch[1];
	} catch (error) {
		console.error('Error extracting ASIN:', error);
		throw error;
	}
}

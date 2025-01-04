// https://amzn.to/42051PN のような記法を B01M2BOZDL に変換する
// https://www.amazon.co.jp/-/en/gp/product/B08WT889V3?ie=UTF8&psc=1&linkCode=sl1&tag=tokuhirom-22&linkId=30e33501a13966a30b888de5b4aef836&language=en_US&ref_=as_li_ss_tl
// https://www.amazon.co.jp/%E3%83%8A%E3%82%AC%E3%82%AA-%E7%87%95%E4%B8%89%E6%9D%A1-%E3%82%B9%E3%83%86%E3%83%B3%E3%83%AC%E3%82%B9-%E3%83%87%E3%82%A3%E3%83%8A%E3%83%BC%E3%83%95%E3%82%A9%E3%83%BC%E3%82%AF-5%E6%9C%AC/dp/B01M2BOZDL?_encoding=UTF8&pd_rd_w=a6agn&content-id=amzn1.sym.bcc66df3-c2cc-4242-967e-174aec86af7a:amzn1.symc.a9cb614c-616d-4684-840d-556cb89e228d&pf_rd_p=bcc66df3-c2cc-4242-967e-174aec86af7a&pf_rd_r=14B098QT0WFV8K1YYMA0&pd_rd_wg=N1Vqd&pd_rd_r=0b99f5ff-8cf1-4ccb-84d0-e958cc8f64f5&th=1&linkCode=sl1&tag=tokuhirom-22&linkId=0bb114c89147942275c86c1dc5efbe4f&language=ja_JP&ref_=as_li_ss_tl
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
		const asinMatch = location.match(/\/(dp|product)\/([A-Z0-9]{10})/);
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

// getitems.pl の出力の型
export interface AmazonImage {
	Height: number;
	URL: string;
	Width: number;
}

export interface AmazonTitle {
	DisplayValue: string;
	Label: string;
	Locale: string;
}

export interface AmazonItemInfo {
	Title?: AmazonTitle;
}

export interface AmazonImages {
	Primary?: {
		Medium?: AmazonImage;
	};
}

export interface AmazonItem {
	ASIN: string;
	DetailPageURL: string;
	Images?: AmazonImages;
	ItemInfo?: AmazonItemInfo;
}

export interface AmazonItemsResult {
	Items: AmazonItem[];
}

export interface GetItemsResponse {
	ItemsResult: AmazonItemsResult;
}

/**
 * Send PubSubHubbub notificaiton to the Hub.
 * @param hubUrl Hub URL (e.g.: https://pubsubhubbub.appspot.com/)
 * @param feedUrl Feed URL (e.g.: https://example.com/feed)
 */
export async function notifyHub(hubUrl: string, feedUrl: string): Promise<void> {
	try {
		const response = await fetch(hubUrl, {
			method: 'POST',
			headers: {
				'Content-Type': 'application/x-www-form-urlencoded'
			},
			body: new URLSearchParams({
				'hub.mode': 'publish',
				'hub.url': feedUrl
			})
		});

		if (!response.ok) {
			throw new Error(`Hub notification failed: ${response.status} ${response.statusText}`);
		}

		console.log(`Notification sent to Hub: ${hubUrl}`);
	} catch (error) {
		console.error(`Failed to notify Hub:`, error);
	}
}

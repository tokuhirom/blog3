#!/usr/bin/env bun

async function createEntry(): Promise<string> {
	const response = await fetch('http://localhost:5173/api/entry', {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json'
		},
		body: JSON.stringify({
			title: 'Test Entry',
			body: 'This is a test entry body.',
			status: 'draft'
		})
	});

	if (!response.ok) {
		console.error(`Error: ${response.status} ${response.statusText}`);
		const errorBody = await response.text();
		console.error(errorBody);
		process.exit(1);
	}

	const result = await response.json();
	console.log('Entry created successfully:', result);
	const path = result.entry.path;
	if (!path) {
		console.error('Error: Entry path not found');
		process.exit(1);
	}
	return path;
}

async function updateEntry(path: string) {
	console.log('Updating entry with path:', path);
	const encodedPath = encodeURIComponent(path);
	const response = await fetch(`http://localhost:5173/api/entry/${encodedPath}`, {
		method: 'PATCH',
		headers: {
			'Content-Type': 'application/json'
		},
		body: JSON.stringify({
			title: 'Updated Test Entry',
			body: 'This is an updated test entry body.',
			status: 'draft'
		})
	});

	if (!response.ok) {
		console.error(`Error: ${response.status} ${response.statusText}`);
		const errorBody = await response.text();
		console.error(errorBody);
		process.exit(1);
	}

	const result = await response.json();
	console.log('Entry updated successfully:', result);
}

function deleteEntry(path: string) {
	console.log('Deleting entry with path:', path);
	const encodedPath = encodeURIComponent(path);
	fetch(`http://localhost:5173/api/entry/${encodedPath}`, {
		method: 'DELETE'
	})
		.then((response) => {
			if (!response.ok) {
				console.error(`Error: ${response.status} ${response.statusText}`);
				return response.text().then((errorBody) => {
					console.error(errorBody);
				});
			}
			console.log('Entry deleted successfully');
		})
		.catch((error) => {
			console.error('Failed to delete entry:', error);
		});
}

const path = await createEntry();
await updateEntry(path);
await deleteEntry(path);

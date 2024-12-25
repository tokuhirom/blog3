<script lang="ts">
	import MarkdownEditor from '$lib/components/admin/MarkdownEditor.svelte';

	let form: HTMLFormElement;

	let title: string = '';
	let body: string = '';
	let status: 'draft' | 'published' = 'draft';

	function handleSubmit(event: Event) {
		event.preventDefault();

		const data = {
			title,
			body,
			status
		};

		fetch('/admin/api/entry', {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json'
			},
			body: JSON.stringify(data)
		})
			.then((response) => {
				if (response.ok) {
					location.href = '/admin';
				} else {
					console.log(response);
					alert('Failed to create entry');
				}
			})
			.catch((error) => {
				console.error(error);
				alert('Failed to create entry');
			});
	}
</script>

<form bind:this={form} method="post" class="form" onsubmit={handleSubmit}>
	<div>
		<label for="title" class="label">Title</label>
		<input id="title" name="title" type="text" class="input" bind:value={title} required />
	</div>

	<div class="editor">
		<label for="body" class="label">Body</label>
		<MarkdownEditor
			initialContent={body}
			onUpdateText={(content) => {
				body = content;
				console.log(body);
			}}
			onSave={(content) => {
				body = content;
				console.log('Save the content by shortcut');
				form.submit();
			}}
		></MarkdownEditor>
	</div>

	<div>
		<label for="status" class="label">Status</label>
		<select id="status" name="status" class="select" bind:value={status}>
			<option value="draft">Draft</option>
			<option value="published">Published</option>
		</select>
	</div>

	<div class="button-container">
		<button type="submit" class="create-button">Create Entry</button>
	</div>
</form>

<style>
	.form {
		display: flex;
		flex-direction: column;
		gap: 1rem;
		padding: 1rem;
	}

	.label {
		display: block;
		font-size: 0.875rem;
		font-weight: 500;
		color: #4a5568;
	}

	.input {
		width: 100%;
		border-radius: 0.375rem;
		border: 1px solid #d1d5db;
		padding: 0.5rem;
	}

	.editor {
		border: 1px solid #d1d5db;
		border-radius: 0.25rem;
		height: 400px;
		overflow-y: scroll;
	}

	.select {
		width: 100%;
		border-radius: 0.375rem;
		border: 1px solid #d1d5db;
		padding: 0.5rem;
	}

	.button-container {
		display: flex;
		justify-content: space-between;
	}

	.create-button {
		border-radius: 0.375rem;
		background-color: #3b82f6;
		padding: 0.5rem 1rem;
		color: white;
	}

	.create-button:hover {
		background-color: #2563eb;
	}
</style>

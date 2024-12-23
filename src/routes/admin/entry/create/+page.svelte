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

<form bind:this={form} method="post" class="space-y-4 p-4" onsubmit={handleSubmit}>
	<div>
		<label for="title" class="block text-sm font-medium text-gray-700">Title</label>
		<input
			id="title"
			name="title"
			type="text"
			class="w-full rounded border p-2"
			bind:value={title}
			required
		/>
	</div>

	<div class="editor">
		<label for="body" class="block text-sm font-medium text-gray-700">Body</label>
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
		<label for="status" class="block text-sm font-medium text-gray-700">Status</label>
		<select id="status" name="status" class="w-full rounded border p-2" bind:value={status}>
			<option value="draft">Draft</option>
			<option value="published">Published</option>
		</select>
	</div>

	<div class="flex justify-between">
		<button type="submit" class="rounded bg-blue-500 px-4 py-2 text-white hover:bg-blue-600">
			Create Entry
		</button>
	</div>
</form>

<style>
	.editor {
		border: 1px solid #d1d5db;
		border-radius: 0.25rem;
		height: 400px;
		overflow-y: scroll;
	}
</style>

<script lang="ts">
</script>

<form
	method="post"
	action="?/create"
	class="space-y-4 p-4"
>
	<div>
		<label for="title" class="block text-sm font-medium text-gray-700">Title</label>
		<input
			id="title"
			name="title"
			type="text"
			class="w-full rounded border p-2"
			required
		/>
	</div>

	<div>
		<label for="body" class="block text-sm font-medium text-gray-700">Body</label>
		<textarea
			id="body"
			name="body"
			class="w-full rounded border p-2"
			rows="10"
			required
		></textarea>
	</div>

	<div>
		<label for="status" class="block text-sm font-medium text-gray-700">Status</label>
		<select id="status" name="status" class="w-full rounded border p-2">
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

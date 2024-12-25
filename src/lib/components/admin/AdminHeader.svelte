<script lang="ts">
	async function handleNewEntry() {
		fetch('/admin/api/entry', {
			method: 'POST'
		})
			.then(async (response) => {
				if (response.ok) {
					const data = await response.json();
					location.href = `/admin/entry/${encodeURIComponent(data.path)}`;
				} else {
					console.error('Failed to create new entry');
					alert('Failed to create new entry. Cannot get new entry path');
				}
			})
			.catch((err) => {
				console.error('Error creating new entry:', err);
				alert('Failed to create new entry');
			});
	}
</script>

<header>
	<div class="container">
		<!-- トップページへのリンク -->
		<a href="/admin/" class="text-xl">Blog Admin</a>

		<!-- ナビゲーションリンク -->
		<nav>
			<a onclick={handleNewEntry}>New Entry</a>
		</nav>
	</div>
</header>

<style>
	header {
		position: fixed;
		left: 0;
		top: 0;
		z-index: 10;
		width: 100%;
		background-color: #d97706;
		padding: 1rem;
		color: white;
	}

	.container {
		max-width: 1200px;
		margin: 0 auto;
		display: flex;
		align-items: center;
		justify-content: space-between;
	}

	a {
		text-decoration: none;
		color: white;
	}

	a:hover {
		text-decoration: underline;
	}

	.text-xl {
		font-size: 1.25rem;
		font-weight: bold;
	}

	nav {
		display: flex;
		gap: 1rem;
	}
</style>

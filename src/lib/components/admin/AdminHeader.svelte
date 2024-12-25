<script>
</script>

<header>
	<div class="container">
		<!-- トップページへのリンク -->
		<a href="/admin/" class="text-xl">Blog Admin</a>

		<!-- ナビゲーションリンク -->
		<nav>
			<a href="/admin/entry/create">New Entry</a>
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

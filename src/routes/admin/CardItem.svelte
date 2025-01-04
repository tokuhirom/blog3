<script lang="ts">
	export let title: string | undefined;
	export let content: string | undefined;
	export let onClick: (event: MouseEvent) => void;
	export let backgroundColor = '#f6f6f6';
	export let color = '#0f0f0f';

	export let imgSrc: string | undefined = undefined;
</script>

<button
	class="card"
	style:background-color={backgroundColor}
	style:color
	on:click|preventDefault={onClick}
>
	{#if title}
		<span class="title">{title}</span>
	{/if}
	{#if imgSrc}
		<!-- eslint-disable-next-line a11y-img-redundant-alt -->
		<img
			src={imgSrc}
			alt="great one"
			style="max-width: 100%;max-height: 100%; border: 1px solid #000;"
		/>
	{/if}
	{#if title}
		<span class="content">{content}</span>
	{/if}
</button>

<style>
	.card {
		display: flex; /* Flexboxを有効にします */
		flex-direction: column; /* アイテムを縦方向に並べます */
		align-items: flex-start; /* 左寄せにします（テキストが左揃えの場合） */
		justify-content: flex-start; /* アイテムを上に寄せます */
		float: left;
		width: 180px;
		height: 180px;
		margin: 9px;
		padding: 9px;
		overflow-y: hidden;
		border-radius: 2px;
		text-align: left;
		vertical-align: top;
		overflow-x: hidden;
		cursor: pointer;
	}

	.title {
		font-size: 0.875rem;
		font-weight: bold;
		word-break: break-all;
	}

	.content {
		margin-top: 4px;
		color: #4b5563;
		font-size: 0.675rem;
	}
</style>

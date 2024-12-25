// これは、Date オブジェクトを MySQL の DATETIME 形式の文字列に変換する関数です。
// Date オブジェクトが null の場合は、null を返します。
// これを手書きで書く必要があるのかは謎。。
export function formatDateForMySQL(date: Date | null): string | null {
	if (!date) return null; // nullの場合もサポート
	const pad = (n: number) => n.toString().padStart(2, '0');
	return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`;
}

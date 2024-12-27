import { S3Client, PutObjectCommand } from '@aws-sdk/client-s3';
import * as dotenv from 'dotenv';

dotenv.config();

// 環境変数から設定を取得
const REGION = process.env.S3_REGION || 'jp-north-1';
const BUCKET_NAME = process.env.S3_ATTACHMENTS_BUCKET_NAME || 'blog3-attachments';
const ACCESS_KEY_ID: string | undefined = process.env.S3_ACCESS_KEY_ID;
const SECRET_ACCESS_KEY: string | undefined = process.env.S3_SECRET_ACCESS_KEY;
const ENDPOINT = process.env.S3_ENDPOINT || 'https://s3.isk01.sakurastorage.jp';

if (!ACCESS_KEY_ID || !SECRET_ACCESS_KEY) {
	console.error('S3 credentials are not set');
	process.exit(1);
}

// S3 クライアントの初期化
export const s3 = new S3Client({
	region: REGION,
	endpoint: ENDPOINT,
	credentials: {
		accessKeyId: ACCESS_KEY_ID,
		secretAccessKey: SECRET_ACCESS_KEY
	}
});

// ファイルを S3 にアップロードする関数
export async function putAttachments(
	key: string, // S3 バケット内のファイルパス
	body: Buffer | Uint8Array | Blob | string, // ファイルの内容
	contentType: string // ファイルの MIME タイプ
): Promise<void> {
	try {
		const command = new PutObjectCommand({
			Bucket: BUCKET_NAME,
			Key: key,
			Body: body,
			ContentType: contentType
		});

		await s3.send(command);
		console.log(`File uploaded successfully to ${key}`);
	} catch (error) {
		console.error('Error uploading file to S3:', error);
		throw error;
	}
}

import fs from 'fs';
import { exec } from 'child_process';
import { putObject } from './s3';
import { BACKUP_BUCKET_NAME } from '$lib/config';

async function takeBackup(encryption_key: string) {
	console.log('takeBackup');

	// generate dump file path
	const date = new Date();
	const dump_file_name = `/tmp/blog3-backup-${date.toISOString().replace(/:/g, '-')}.sql`;
	const encrypted_file_name = `${dump_file_name}.enc`;
	console.log('mysqldump file name:', dump_file_name);

	try {
		// mysqldump コマンドを実行してバックアップを取る
		await execCommand(
			`mysqldump --set-gtid-purged=OFF --host=${process.env.DATABASE_HOST} --port=${process.env.DATABASE_PORT} --user=${process.env.DATABASE_USER} --password=${process.env.DATABASE_PASSWORD} ${process.env.DATABASE_NAME} > ${dump_file_name}`
		);

		// openssl コマンドで共通鍵暗号で暗号化する
		await execCommand(
			`openssl enc -aes-256-cbc -salt -in ${dump_file_name} -out ${encrypted_file_name} -pass pass:${encryption_key}`
		);
		console.log(`Encrypted file created: ${encrypted_file_name}`);

		// ファイルを S3 にアップロード
		console.log(`uploading file to S3: ${encrypted_file_name}`);
		const fileContent = await fs.promises.readFile(encrypted_file_name);
		await putObject(
			`backups/${encrypted_file_name.split('/').pop()}`,
			fileContent,
			'application/octet-stream',
			BACKUP_BUCKET_NAME
		);
		console.log(`File uploaded to S3: ${encrypted_file_name}`);

		// mysqldump のファイルを掃除する
		console.log(`remove temporary file: ${dump_file_name}, ${encrypted_file_name}`);
		await fs.promises.unlink(dump_file_name);
		await fs.promises.unlink(encrypted_file_name);
	} catch (e) {
		console.error('Error taking backup:', e);
	}
}

function execCommand(command: string): Promise<string> {
	return new Promise((resolve, reject) => {
		exec(command, (error, stdout, stderr) => {
			if (error) {
				reject(error);
			} else {
				resolve(stdout || stderr);
			}
		});
	});
}

export function startBackup(encrytpion_key: string) {
	console.log('Start taking backup');
	setTimeout(() => takeBackup(encrytpion_key), 1000);
	setInterval(() => takeBackup(encrytpion_key), 1000 * 60 * 60 * 24); // 24 時間ごとにバックアップを取る
}

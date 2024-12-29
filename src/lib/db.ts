import mysql, { type Pool } from 'mysql2/promise';
import * as dotenv from 'dotenv';

dotenv.config();

// Check the environment variables
const requiredEnvVars = [
	'DATABASE_HOST',
	'DATABASE_PORT',
	'DATABASE_USER',
	'DATABASE_PASSWORD',
	'DATABASE_NAME'
];
requiredEnvVars.forEach((varName) => {
	if (!process.env[varName]) {
		console.error(`Required environment variable '${varName}' is missing`);
		// Exit the application
		process.exit(1);
	}
});
console.log(`Connecting to database at ${process.env.DATABASE_HOST}`);

export const db: Pool = mysql.createPool({
	host: process.env.DATABASE_HOST!,
	port: Number(process.env.DATABASE_PORT),
	user: process.env.DATABASE_USER!,
	password: process.env.DATABASE_PASSWORD!,
	database: process.env.DATABASE_NAME!,
	dateStrings: true
});

export type Entry = {
	path: string;
	title: string;
	body: string;
	visibility: 'private' | 'public';
	format: 'html' | 'mkdn';
	created_at: string;
	updated_at: string | null;
	published_at: string | null;
};

export type EntryLink = {
	src_path: string;
	dst_title: string;
};

export type EntryLinkWithPath = {
	src_path: string;
	dst_title: string;
	dst_path: string;
};

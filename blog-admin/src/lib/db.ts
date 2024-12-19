import mysql, {type Pool } from 'mysql2/promise';
import * as dotenv from 'dotenv';
dotenv.config();

export const db: Pool = mysql.createPool({
    host: process.env.DATABASE_HOST!,
    port: Number(process.env.DATABASE_PORT),
    user: process.env.DATABASE_USER!,
    password: process.env.DATABASE_PASSWORD!,
    database: process.env.DATABASE_NAME!,
});

export type Entry = {
    path: string;
    title: string;
    body: string;
    status: 'draft' | 'published';
    format: 'html' | 'mkdn';
    created_at: Date;
    updated_at: Date | null;
};

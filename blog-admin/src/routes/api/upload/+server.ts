import { json } from '@sveltejs/kit';
import { putObject } from '$lib/s3';
import type { RequestHandler } from './$types';

export const POST: RequestHandler = async ({ request }) => {
  const formData = await request.formData();
  const file = formData.get('file') as File;

  if (!file) {
    return json({ error: 'File is required' }, { status: 400 });
  }

  try {
    const key = `${Date.now()}-${file.name}`;
    const buffer = await file.arrayBuffer();
    const mimeType = file.type;

    await putObject(key, Buffer.from(buffer), mimeType);

    const publicUrl = `https://blog-attachments.64p.org/${key}`;

    return json({ url: publicUrl });
  } catch (error) {
    console.error('Failed to upload file:', error);
    return json({ error: 'Failed to upload file' }, { status: 500 });
  }
};

import MarkdownIt from 'markdown-it';
import type { Entry } from './db';

const md = new MarkdownIt();

export function renderHTML(markdown: string): string {
    return md.render(markdown);
}

export function renderHTMLByEntry(entry: Entry): string {
    if (entry.format === 'mkdn') {
        return renderHTML(entry.body);
    } else {
        console.log('Unsupported format: ', entry.format);
        return entry.body;
    }
}

import { describe, it, expect } from 'vitest';
import { renderHTML } from './server/markdown';

describe('renderHTML', () => {
	it('should ignore HTML comments', () => {
		const markdown = 'Content\n<!-- This is a comment -->\nMore content';
		const expectedHtml = '<p>Content</p>\n<p>More content</p>\n';
		const result = renderHTML(markdown, {});
		console.log(result);
		expect(result).toBe(expectedHtml);
	});
});

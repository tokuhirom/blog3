package blog3.entity

import com.vladsch.flexmark.ext.autolink.AutolinkExtension
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.data.MutableDataSet
import java.time.LocalDateTime

class MarkdownRenderer(
    private val parser: Parser,
    private val renderer: HtmlRenderer,
) {
    fun render(mkdn: String): String {
        val document = parser.parse(mkdn)
        return renderer.render(document)
    }

    companion object {
        @JvmStatic
        fun build(): MarkdownRenderer {
            val options = MutableDataSet()
            options.set(Parser.EXTENSIONS, listOf(AutolinkExtension.create()))
            val parser = Parser.builder(options).build()
            val renderer = HtmlRenderer.builder(options).build()
            return MarkdownRenderer(parser, renderer)
        }
    }
}

val renderer = MarkdownRenderer.build()

data class Entry(
    val path: String,
    val title: String,
    val body: String,
    val status: String,
    val format: String, // TODO make this enum
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?,
) {
    fun html(): String =
        if (format == "mkdn") {
            renderer.render(body)
        } else {
            body
        }
}

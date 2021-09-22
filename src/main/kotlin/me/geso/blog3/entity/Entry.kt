package me.geso.blog3.entity

import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.data.MutableDataSet
import org.jetbrains.annotations.NotNull
import java.time.LocalDateTime

var options: MutableDataSet = MutableDataSet()
var parser: Parser = Parser.builder(options).build()
var renderer: HtmlRenderer = HtmlRenderer.builder(options).build()

data class Entry(
    val path: String,
    val title: String,
    val body: String,
    val status: String,
    val format: String, // TODO make this enum
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?,
) {
    fun html(): String {
        return if (format == "mkdn") {
            val document = parser.parse(body)
            renderer.render(document)
        } else {
            body
        }
    }
}

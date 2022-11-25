package blog3.admin.plugin

import kotlinx.serialization.json.JsonObject
import kweb.TextAreaElement
import kweb.plugins.KwebPlugin
import kweb.util.json
import org.jsoup.nodes.Document

class EasyMDEPlugin : KwebPlugin() {
    override fun decorate(doc: Document) {
        super.decorate(doc)
        doc.head()
            .appendElement("link")
            .attr("rel", "stylesheet")
            .attr("href", "https://cdn.jsdelivr.net/npm/easymde/dist/easymde.min.css")

        doc.head()
            .appendElement("script")
            .attr("src", "https://cdn.jsdelivr.net/npm/easymde/dist/easymde.min.js")
    }
}

fun TextAreaElement.easyMDE(opts: JsonObject) {
    browser.callJsFunction(
        """
            const config = {};
            config["element"] = document.getElementById({});
            window.easyMDE = new EasyMDE(config);
        """.trimIndent(), opts, this.id.json
    )
}

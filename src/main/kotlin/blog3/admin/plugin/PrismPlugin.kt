package blog3.admin.plugin

import kweb.plugins.KwebPlugin
import org.jsoup.nodes.Document

class PrismPlugin : KwebPlugin() {
    override fun decorate(doc: Document) {
        doc.head().appendElement("link")
            .attr("href", "https://cdn.jsdelivr.net/npm/prismjs@1/themes/prism-solarizedlight.min.css")
            .attr("rel", "stylesheet")
        doc.head().appendElement("link")
            .attr("href", "https://cdn.jsdelivr.net/npm/prismjs@1/themes/prism.min.css")
            .attr("rel", "stylesheet")

        doc.body().appendElement("script")
            .attr("src", "https://cdn.jsdelivr.net/npm/prismjs@1/prism.min.js")
        doc.body().appendElement("script")
            .attr("src", "https://cdn.jsdelivr.net/npm/prismjs@1/plugins/autoloader/prism-autoloader.min.js")
    }
}

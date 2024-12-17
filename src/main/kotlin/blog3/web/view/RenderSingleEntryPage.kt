package blog3.web.view

import blog3.entity.Entry
import io.ktor.server.routing.RoutingContext
import org.springframework.boot.info.GitProperties

suspend fun RoutingContext.renderSingleEntryPage(
    entry: Entry,
    gitProperties: GitProperties,
) {
    publicWrapper("${entry.title} - tokuhirom's blog", gitProperties) {
        renderEntry(entry)
        renderAd()
    }
}

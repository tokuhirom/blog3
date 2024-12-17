package blog3.web.view

import blog3.entity.Entry
import io.ktor.server.routing.RoutingContext
import org.springframework.boot.info.GitProperties
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

suspend fun RoutingContext.renderSearchPage(
    query: String,
    entries: List<Entry>,
    page: Int,
    gitProperties: GitProperties,
) {
    publicWrapper("$query - tokuhirom's blog", gitProperties) {
        entries.forEach { entry ->
            renderEntry(entry)
            renderAd()
        }

        pager("/search?q=${URLEncoder.encode(query, StandardCharsets.UTF_8)}&page=", page)
    }
}

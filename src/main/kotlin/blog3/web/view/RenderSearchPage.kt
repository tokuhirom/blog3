package blog3.web.view

import blog3.entity.Entry
import io.ktor.server.application.ApplicationCall
import io.ktor.util.pipeline.PipelineContext
import org.springframework.boot.info.GitProperties
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

suspend fun PipelineContext<Unit, ApplicationCall>.renderSearchPage(
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


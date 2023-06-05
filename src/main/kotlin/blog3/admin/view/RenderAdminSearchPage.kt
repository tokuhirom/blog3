package blog3.admin.view

import blog3.admin.view.parts.adminEntriesTable
import blog3.admin.view.parts.adminPager
import blog3.admin.view.parts.adminWrapper
import blog3.admin.view.parts.searchBox
import blog3.entity.Entry
import io.ktor.server.application.ApplicationCall
import io.ktor.util.pipeline.PipelineContext
import org.springframework.boot.info.GitProperties
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

suspend fun PipelineContext<Unit, ApplicationCall>.renderAdminSearchPage(
    query: String,
    page: Int,
    entries: List<Entry>,
    gitProperties: GitProperties,
) {
    adminWrapper("$query - tokuhirom's blog", gitProperties) {
        searchBox(query)

        adminEntriesTable(entries)

        adminPager("/search?q=${URLEncoder.encode(query, StandardCharsets.UTF_8)}&page=", page)
    }
}


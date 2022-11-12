package blog3.web.view

import blog3.entity.Entry
import io.ktor.server.application.ApplicationCall
import io.ktor.util.pipeline.PipelineContext
import org.springframework.boot.info.GitProperties

suspend fun PipelineContext<Unit, ApplicationCall>.renderIndexPage(
    entries: List<Entry>,
    page: Int,
    gitProperties: GitProperties,
) {
    publicWrapper("tokuhirom's blog", gitProperties) {
        entries.forEach { entry ->
            renderEntry(entry)
            renderAd()
        }

        pager("/?page=", page)
    }
}


package blog3.web.view

import blog3.entity.Entry
import io.ktor.server.application.ApplicationCall
import io.ktor.util.pipeline.PipelineContext
import org.springframework.boot.info.GitProperties

suspend fun PipelineContext<Unit, ApplicationCall>.renderSingleEntryPage(
    entry: Entry,
    gitProperties: GitProperties,
) {
    publicWrapper("tokuhirom's blog", gitProperties) {
        renderEntry(entry)
        renderAd()
    }
}


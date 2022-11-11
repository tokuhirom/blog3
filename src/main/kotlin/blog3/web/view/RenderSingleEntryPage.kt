package blog3.web.view

import io.ktor.server.application.*
import io.ktor.util.pipeline.*
import blog3.entity.Entry
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


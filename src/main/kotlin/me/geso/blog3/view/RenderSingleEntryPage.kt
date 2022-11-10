package me.geso.blog3.view

import io.ktor.server.application.*
import io.ktor.util.pipeline.*
import me.geso.blog3.entity.Entry
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


package me.geso.blog3.web.view

import io.ktor.server.application.*
import io.ktor.util.pipeline.*
import me.geso.blog3.entity.Entry
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


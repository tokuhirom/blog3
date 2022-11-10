package me.geso.blog3.web

import com.rometools.rome.io.WireFeedOutput
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import me.geso.blog3.web.service.FeedService
import me.geso.blog3.web.service.PublicEntryService
import me.geso.blog3.web.view.renderIndexPage
import me.geso.blog3.web.view.renderSearchPage
import me.geso.blog3.web.view.renderSingleEntryPage
import org.slf4j.event.Level
import org.springframework.boot.info.GitProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit


class UserSideServer(
    private val gitProperties: GitProperties,
    private val publicEntryService: PublicEntryService,
    private val feedService: FeedService,
) {
    private val feedContentType = ContentType.parse("application/rss+xml;charset=UTF-8")

    private val server = embeddedServer(Netty, port = 8180) {
        install(DefaultHeaders)

        install(CallLogging) {
            level = Level.INFO
        }

        val limit = 20

        routing {
            static("/css") {
                staticBasePackage = "static"
                resources("css")
            }

            get("/") {
                val page = (call.request.queryParameters["page"] ?: "1").toInt()
                val entries = publicEntryService.findPublicEntries(page, limit)

                renderIndexPage(entries, page, gitProperties)
            }

            get("/search") {
                val query = call.request.queryParameters["q"] ?: throw BadRequestException("Missing query parameter")
                val page = (call.request.queryParameters["page"] ?: "1").toInt()
                val entries = publicEntryService.findPublishedByKeyword(query, page, limit)

                renderSearchPage(query, entries, page, gitProperties)
            }

            get("/entry/{keys...}") {
                // I want to get raw keys
                val path = call.request.path().substring("/entry/".length)

                val entry = publicEntryService.findPublicEntryByPath(path)
                    ?: throw NotFoundException("Unknown entry: $path")

                renderSingleEntryPage(entry, gitProperties)
            }

            get("/feed") {
                val entries = publicEntryService.findPublicEntries(
                    page = 1,
                    limit = 10
                )

                val feedString = feedService.buildString(entries)
                call.respondText(feedString, feedContentType)
            }
        }
    }.start(wait = false)


    fun stop() {
        server.stop(1, 1, TimeUnit.SECONDS)
    }
}


@Configuration(proxyBeanMethods = false)
class KtorRootConfiguration {
    @Bean(destroyMethod = "stop")
    fun init(
        gitProperties: GitProperties,
        publicEntryService: PublicEntryService,
        feedService: FeedService
    ): UserSideServer {
        return UserSideServer(gitProperties, publicEntryService, feedService)
    }
}

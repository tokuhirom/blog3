package blog3.web

import blog3.web.service.FeedService
import blog3.web.service.PublicEntryService
import blog3.web.view.renderIndexPage
import blog3.web.view.renderSearchPage
import blog3.web.view.renderSingleEntryPage
import io.ktor.http.ContentType
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.engine.stop
import io.ktor.server.http.content.staticResources
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.NotFoundException
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.defaultheaders.DefaultHeaders
import io.ktor.server.request.path
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import org.slf4j.event.Level
import org.springframework.boot.info.GitProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
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
            staticResources("/css", "static.css")

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
    }


    fun start(wait: Boolean) {
        server.start(wait = wait)
    }

    fun stop() {
        server.stop(1, 1, TimeUnit.SECONDS)
    }
}


@Configuration(proxyBeanMethods = false)
class KtorRootConfiguration {
    @Bean(destroyMethod = "stop")
    @Profile("!test")
    fun userSideServer(
        gitProperties: GitProperties,
        publicEntryService: PublicEntryService,
        feedService: FeedService
    ): UserSideServer {
        return UserSideServer(gitProperties, publicEntryService, feedService)
    }
}

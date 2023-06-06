package blog3.admin

import blog3.admin.service.AdminEntryService
import blog3.admin.service.S3Service
import blog3.admin.view.renderAdminCreatePage
import blog3.admin.view.renderAdminEditPage
import blog3.admin.view.renderAdminIndexPage
import blog3.admin.view.renderAdminS3BucketListPage
import blog3.admin.view.renderAdminSearchPage
import com.amazonaws.services.s3.model.ObjectMetadata
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.readAllParts
import io.ktor.http.content.streamProvider
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.engine.stop
import io.ktor.server.http.content.staticResources
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.NotFoundException
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.defaultheaders.DefaultHeaders
import io.ktor.server.request.contentType
import io.ktor.server.request.path
import io.ktor.server.request.receiveMultipart
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.server.util.getOrFail
import mu.two.KotlinLogging
import org.slf4j.event.Level
import org.springframework.boot.info.GitProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import java.util.concurrent.TimeUnit


class AdminSideServer(
    private val gitProperties: GitProperties,
    private val adminEntryService: AdminEntryService,
    private val s3Service: S3Service,
) {
    private val keyPrefixFormatter = DateTimeFormatter.ofPattern("YYYYMMdd-HHmmss")
    private val logger = KotlinLogging.logger {}

    @Suppress("ExtractKtorModule")
    private val server = embeddedServer(Netty, port = 8280) {
        install(DefaultHeaders)

        install(CallLogging) {
            level = Level.INFO
        }

        install(ContentNegotiation) {
            json()
        }

        val limit = 100

        routing {
            staticResources("/css", "static.css")

            get("/") {
                val page = (call.request.queryParameters["page"] ?: "1").toInt()
                val entries = adminEntryService.findEntries(page, limit)

                renderAdminIndexPage(entries, page, gitProperties)
            }

            get("/search") {
                val query = call.request.queryParameters["q"] ?: throw BadRequestException("Missing query parameter")
                val page = (call.request.queryParameters["page"] ?: "1").toInt()
                val entries = adminEntryService.findByKeyword(query, page, limit)

                renderAdminSearchPage(query, page, entries, gitProperties)
            }

            get("/entry/create") {
                renderAdminCreatePage(gitProperties)
            }

            post("/entry/create") {
                val params = call.receiveParameters()
                adminEntryService.create(
                    params.getOrFail<String>("title"),
                    params.getOrFail<String>("body"),
                    params.getOrFail<String>("status")
                )
                call.respondRedirect("/")
            }

            get("/entry/{keys...}") {
                // I want to get raw keys
                val path = call.request.path().substring("/entry/".length)

                val entry = adminEntryService.findByPath(path)
                    ?: throw NotFoundException("Unknown entry: $path")

                renderAdminEditPage(entry, gitProperties)
            }

            post("/entry/update") {
                val params = call.receiveParameters()
                adminEntryService.update(
                    path = params.getOrFail<String>("path"),
                    title = params.getOrFail<String>("title"),
                    body = params.getOrFail<String>("body"),
                    status = params.getOrFail<String>("status")
                )
                call.respondRedirect("/")
            }

            post("/upload_attachments") {
                val imagePart = (
                        call.receiveMultipart()
                            .readAllParts()
                            .firstOrNull { it.name == "image" }
                            ?: error("Missing 'image' parameter")
                        ) as PartData.FileItem

                logger.info {
                    "Caught file upload request to /upload_attachments:" +
                            " type=${imagePart.contentType}"
                }

                val key = LocalDateTime.now().format(keyPrefixFormatter) + UUID.randomUUID()
                    .toString() + ".${imagePart.contentType!!.contentSubtype}"

                imagePart.streamProvider().use { inputStream ->
                    val bytes = inputStream.readAllBytes()
                    val objectMetadata = ObjectMetadata()
                    objectMetadata.contentType = call.request.contentType().toString()
                    objectMetadata.contentLength = bytes.size.toLong()

                    val url = s3Service.upload(key, bytes.inputStream(), objectMetadata)

//                 {"data": {"filePath": "<filePath>"}}
                    call.respond(
                        HttpStatusCode.OK,
                        mapOf(
                            "data" to mapOf(
                                "filePath" to url
                            )
                        )
                    )
                }
            }

            get("/s3/buckets") {
                renderAdminS3BucketListPage(s3Service.listBuckets(), gitProperties)
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
class KtorAdminConfiguration {
    @Bean(destroyMethod = "stop")
    @Profile("!test")
    fun adminSideServer(
        gitProperties: GitProperties,
        adminEntryService: AdminEntryService,
        s3Service: S3Service,
    ): AdminSideServer {
        return AdminSideServer(gitProperties, adminEntryService, s3Service)
    }
}

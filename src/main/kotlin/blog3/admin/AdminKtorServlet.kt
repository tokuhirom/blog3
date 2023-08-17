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
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.basic
import io.ktor.server.http.content.staticResources
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.NotFoundException
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
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
import org.springframework.boot.info.GitProperties
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID


fun Application.setupAdmin(
    adminEntryService: AdminEntryService,
    gitProperties: GitProperties,
    s3Service: S3Service
) {
    val logger = KotlinLogging.logger {}
    val keyPrefixFormatter = DateTimeFormatter.ofPattern("YYYYMMdd-HHmmss")

    install(ContentNegotiation) {
        json()
    }

    install(Authentication) {
        val userName = System.getenv("SPRING_SECURITY_USER_NAME")
        val password = System.getenv("SPRING_SECURITY_USER_PASSWORD")
        if (userName == null) {
            error("There's no SPRING_SECURITY_USER_NAME")
        }
        if (password == null) {
            error("There's no SPRING_SECURITY_USER_PASSWORD")
        }
        basic("auth-basic") {
            realm = "Access to the admin site"
            validate { credentials ->
                if (credentials.name == userName && credentials.password == password) {
                    UserIdPrincipal(credentials.name)
                } else {
                    null
                }
            }
        }
    }

    val limit = 100

    routing {
        authenticate("auth-basic") {
            staticResources("/admin/css", "static.css")

            get("/admin/") {
                val page = (call.request.queryParameters["page"] ?: "1").toInt()
                val entries = adminEntryService.findEntries(page, limit)

                renderAdminIndexPage(entries, page, gitProperties)
            }

            get("/admin/search") {
                val query =
                    call.request.queryParameters["q"] ?: throw BadRequestException("Missing query parameter")
                val page = (call.request.queryParameters["page"] ?: "1").toInt()
                val entries = adminEntryService.findByKeyword(query, page, limit)

                renderAdminSearchPage(query, page, entries, gitProperties)
            }

            get("/admin/entry/create") {
                renderAdminCreatePage(gitProperties)
            }

            post("/admin/entry/create") {
                val params = call.receiveParameters()
                adminEntryService.create(
                    params.getOrFail<String>("title"),
                    params.getOrFail<String>("body"),
                    params.getOrFail<String>("status")
                )
                call.respondRedirect("/admin/")
            }

            get("/admin/entry/{keys...}") {
                // I want to get raw keys
                val path = call.request.path().substring("/admin/entry/".length)

                val entry = adminEntryService.findByPath(path)
                    ?: throw NotFoundException("Unknown entry: $path")

                renderAdminEditPage(entry, gitProperties)
            }

            post("/admin/entry/update") {
                val params = call.receiveParameters()
                adminEntryService.update(
                    path = params.getOrFail<String>("path"),
                    title = params.getOrFail<String>("title"),
                    body = params.getOrFail<String>("body"),
                    status = params.getOrFail<String>("status")
                )
                call.respondRedirect("/admin/")
            }

            post("/admin/upload_attachments") {
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

            get("/admin/s3/buckets") {
                renderAdminS3BucketListPage(s3Service.listBuckets(), gitProperties)
            }
        }
    }
}

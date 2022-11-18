package blog3.admin.plugin

import blog3.admin.S3Service
import com.amazonaws.services.s3.model.ObjectMetadata
import io.ktor.server.application.call
import io.ktor.server.request.contentLength
import io.ktor.server.request.contentType
import io.ktor.server.request.receiveStream
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.post
import kweb.plugins.KwebPlugin
import mu.KotlinLogging
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

class FileUploadPlugin(
    private val path: String,
    private val s3Service: S3Service,
) : KwebPlugin() {
    private val logger = KotlinLogging.logger {}
    private val keyPrefixFormatter = DateTimeFormatter.ofPattern("YYYYMMdd-HHmmss")

    override fun appServerConfigurator(routeHandler: Routing) {
        routeHandler.post(path) {
            logger.info {
                "Caught file upload request to $path:" +
                        " type=${call.request.contentType()}" +
                        " length=${call.request.contentLength()}"
            }

            val key = LocalDateTime.now().format(keyPrefixFormatter) + UUID.randomUUID()
                .toString() + ".${call.request.contentType().contentSubtype}"

            val objectMetadata = ObjectMetadata()
            objectMetadata.contentType = call.request.contentType().toString()
            objectMetadata.contentLength = call.request.contentLength()!!

            val url = s3Service.upload(key, call.receiveStream(), objectMetadata)
            call.respondText(url)
        }
    }
}

//package blog3.admin.plugin
//
//import blog3.admin.service.S3Service
//import com.amazonaws.services.s3.model.ObjectMetadata
//import com.fasterxml.jackson.databind.DeserializationFeature
//import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
//import io.ktor.http.ContentType
//import io.ktor.http.HttpStatusCode
//import io.ktor.http.content.PartData
//import io.ktor.http.content.readAllParts
//import io.ktor.http.content.streamProvider
//import io.ktor.server.application.call
//import io.ktor.server.request.contentType
//import io.ktor.server.request.receiveMultipart
//import io.ktor.server.response.respondText
//import io.ktor.server.routing.Routing
//import io.ktor.server.routing.post
//import kweb.plugins.KwebPlugin
//import mu.two.KotlinLogging
//import java.time.LocalDateTime
//import java.time.format.DateTimeFormatter
//import java.util.UUID
//
// {"data": {"filePath": "<filePath>"}}
//data class EasyMDEUploadResponse(
//    val data: Data,
//) {
//    data class Data(
//        val filePath: String,
//    )
//
//    companion object {
//        fun of(url: String): EasyMDEUploadResponse {
//            return EasyMDEUploadResponse(Data(url))
//        }
//    }
//}
//
// TODO Care about CSRF
//class FileUploadPlugin(
//    private val path: String,
//    private val s3Service: S3Service,
//) : KwebPlugin() {
//    private val logger = KotlinLogging.logger {}
//    private val keyPrefixFormatter = DateTimeFormatter.ofPattern("YYYYMMdd-HHmmss")
//    private val objectMapper = jacksonMapperBuilder()
//        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
//        .build()
//
//    override fun appServerConfigurator(routeHandler: Routing) {
//        routeHandler.post(path) {
//            val imagePart = (
//                    call.receiveMultipart()
//                        .readAllParts()
//                        .firstOrNull { it.name == "image" }
//                        ?: error("Missing 'image' parameter")
//                    ) as PartData.FileItem
//
//            logger.info {
//                "Caught file upload request to $path:" +
//                        " type=${imagePart.contentType}"
//            }
//
//            val key = LocalDateTime.now().format(keyPrefixFormatter) + UUID.randomUUID()
//                .toString() + ".${imagePart.contentType!!.contentSubtype}"
//
//            imagePart.streamProvider().use { inputStream ->
//                val bytes = inputStream.readAllBytes()
//                val objectMetadata = ObjectMetadata()
//                objectMetadata.contentType = call.request.contentType().toString()
//                objectMetadata.contentLength = bytes.size.toLong()
//
//                val url = s3Service.upload(key, bytes.inputStream(), objectMetadata)
//
//                 {"data": {"filePath": "<filePath>"}}
//                call.respondText(
//                    ContentType.parse("application/json"),
//                    HttpStatusCode.OK
//                ) {
//                    objectMapper.writeValueAsString(EasyMDEUploadResponse.of(url))
//                }
//            }
//        }
//    }
//}

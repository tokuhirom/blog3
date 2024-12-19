package blog3.admin.service

import io.ktor.util.decodeBase64String
import mu.two.KotlinLogging
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME

@ConfigurationProperties("content")
data class ContentConfigurationProperties(
    val region: String = "jp-north-1",
    val bucketName: String = "blog3-content",
    val serviceEndpoint: String = "https://s3.isk01.sakurastorage.jp",
)

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(ContentConfigurationProperties::class)
class ContentConfiguration {
    @Bean
    fun contentService(contentConfigurationProperties: ContentConfigurationProperties): ContentService =
        ContentService(
            region = contentConfigurationProperties.region,
            bucketName = contentConfigurationProperties.bucketName,
            serviceEndpoint = contentConfigurationProperties.serviceEndpoint,
        )
}

class ContentService(
    private val region: String,
    private val bucketName: String,
    serviceEndpoint: String,
    credentialProvider: AwsCredentialsProvider = EnvironmentVariableCredentialsProvider.create(),
) {
    private val logger = KotlinLogging.logger {}

    // Build S3 client using ENV[AWS_ACCESS_KEY_ID], ENV[AWS_SECRET_ACCESS_KEY]
    private val s3Client: S3Client =
        S3Client
            .builder()
            .endpointOverride(java.net.URI.create(serviceEndpoint))
            .region(Region.of(region))
            .credentialsProvider(credentialProvider)
            .build()

    fun upload(
        key: String,
        bytes: ByteArray,
        metadata: Map<String, String>,
    ) {
        logger.info("Uploading file: bucketName=$bucketName key=$key region=$region")

        val request =
            PutObjectRequest
                .builder()
                .bucket(bucketName)
                .key(key)
                .metadata(metadata)
                .build()

        s3Client.putObject(
            request,
            RequestBody.fromBytes(bytes),
        )
    }

    fun findAllObjects(continuationToken: String? = null) {
        val startTime = System.currentTimeMillis() // 実行開始時刻を取得

        val request =
            ListObjectsV2Request
                .builder()
                .bucket(bucketName)
//                .maxKeys(30)
//                .continuationToken(continuationToken)
                .build()
        val resp = s3Client.listObjectsV2(request)
        println(resp.nextContinuationToken())
        val entries =
            resp.contents().map { s3Object ->
                val got =
                    s3Client
                        .getObject(
                            GetObjectRequest
                                .builder()
                                .bucket(bucketName)
                                .key(s3Object.key())
                                .build(),
                        )
                val response = got.response()
                // {x-blog3-format=mkdn, content-type=text/plain, x-blog3-title-b64=UGVybCA1LjE5Ljkg44Gn5a6f6KOF44GV44KM44GfIHNpZ25hdHVyZXMg44Gu5qeL5paH44KS44Gf44KB44GX44Gm44G/44KL, x-blog3-status=published}
                val metadata = response.metadata()
                val entry =
                    blog3.entity.Entry(
                        path = s3Object.key(),
                        title = metadata["x-blog3-title-b64"]!!.decodeBase64String(),
                        body = got.readAllBytes().decodeToString(),
                        status = metadata["x-blog3-status"]!!,
                        format = metadata["x-blog3-format"]!!,
                        createdAt = LocalDateTime.parse(metadata["x-blog3-created"]!!, ISO_LOCAL_DATE_TIME),
                        updatedAt =
                            metadata["x-blog3-updated"]?.let {
                                LocalDateTime.parse(it, ISO_LOCAL_DATE_TIME)
                            },
                    )
                entry
            }

        val endTime = System.currentTimeMillis() // 実行終了時刻を取得
        println("findAllObjects completed in ${endTime - startTime} ms. loaded ${entries.size} entries.") // 経過時間をログ出力
    }
}

// fun main() {
//    val client =
//        ContentService(
//            region = "jp-north-1",
//            bucketName = "blog3-content",
//            serviceEndpoint = "https://s3.isk01.sakurastorage.jp",
//        )
//
//     list up entries.
//
//    println(client.upload(key, "haha"))
// }
//

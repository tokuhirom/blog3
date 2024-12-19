package blog3.admin.service

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
import software.amazon.awssdk.services.s3.model.Bucket
import software.amazon.awssdk.services.s3.model.PutObjectRequest

@ConfigurationProperties("s3")
data class S3ConfigurationProperties(
    val region: String = "jp-north-1",
    val bucketName: String = "blog3-attachments",
    val publicDomain: String = "blog-attachments.64p.org",
    val serviceEndpoint: String = "https://s3.isk01.sakurastorage.jp",
)

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(S3ConfigurationProperties::class)
class S3Configuration {
    @Bean
    fun s3Service(s3ConfigurationProperties: S3ConfigurationProperties): S3Service =
        S3Service(
            region = s3ConfigurationProperties.region,
            bucketName = s3ConfigurationProperties.bucketName,
            serviceEndpoint = s3ConfigurationProperties.serviceEndpoint,
            publicDomain = s3ConfigurationProperties.publicDomain,
        )
}

class S3Service(
    private val region: String,
    private val bucketName: String,
    private val publicDomain: String,
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

    fun listBuckets(): List<Bucket> {
        val response = s3Client.listBuckets()
        return response.buckets() ?: emptyList()
    }

    fun upload(
        key: String,
        bytes: ByteArray,
        metadata: Map<String, String>,
    ): String {
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

        return "https://$publicDomain/$key"
    }
}

// fun main() {
//    val client = S3Service(
//        region = "ap-northeast-1", bucketName = "blog3-attachments",
//        publicDomain = "blog-attachments.64p.org"
//    )
//    val key = "hello.txt"
//    println(client.upload(key, "haha"))
// }

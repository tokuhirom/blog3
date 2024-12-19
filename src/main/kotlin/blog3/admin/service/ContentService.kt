package blog3.admin.service

import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.ObjectMetadata
import mu.two.KotlinLogging
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.InputStream

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
    region: String,
    private val bucketName: String,
    serviceEndpoint: String,
    credentialProvider: AWSCredentialsProvider = EnvironmentVariableCredentialsProvider(),
) {
    private val logger = KotlinLogging.logger {}

    // Build S3 client using ENV[AWS_ACCESS_KEY_ID], ENV[AWS_SECRET_KEY]
    private val s3Client =
        AmazonS3ClientBuilder
            .standard()
            .withEndpointConfiguration(AwsClientBuilder.EndpointConfiguration(serviceEndpoint, region))
            .withCredentials(credentialProvider)
            .build()

    fun upload(
        key: String,
        inputStream: InputStream,
        metadata: ObjectMetadata,
    ) {
        logger.info("Uploading file: bucketName=$bucketName key=$key region=${s3Client.regionName}")
        s3Client.putObject(bucketName, key, inputStream, metadata)!!
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

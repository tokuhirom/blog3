package blog3.admin

import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.Bucket
import com.amazonaws.services.s3.model.ObjectMetadata
import mu.KotlinLogging
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.InputStream

@ConstructorBinding
@ConfigurationProperties("s3")
data class S3ConfigurationProperties(
    val region: String = "ap-northeast-1",
    val bucketName: String = "blog3-attachments",
    val publicDomain: String = "blog-attachments.64p.org",
)

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(S3ConfigurationProperties::class)
class S3Configuration {
    @Bean
    fun s3Client(s3ConfigurationProperties: S3ConfigurationProperties): S3Service {
        return S3Service(
            region = s3ConfigurationProperties.region,
            bucketName = s3ConfigurationProperties.bucketName,
            publicDomain = s3ConfigurationProperties.publicDomain,
        )
    }
}

class S3Service(
    region: String,
    private val bucketName: String,
    private val publicDomain: String,
    credentialProvider: AWSCredentialsProvider = EnvironmentVariableCredentialsProvider(),
) {
    private val logger = KotlinLogging.logger {}

    // Build S3 client using ENV[AWS_ACCESS_KEY_ID], ENV[AWS_SECRET_KEY]
    private val s3Client = AmazonS3ClientBuilder.standard()
        .withCredentials(credentialProvider)
        .withRegion(region)
        .build()

    fun listBuckets(): List<Bucket> {
        return s3Client.listBuckets()!!
    }

    fun upload(key: String, content: String): String {
        s3Client.putObject(bucketName, key, content)!!
        return "https://${publicDomain}/${key}"
    }

    fun upload(key: String, inputStream: InputStream, metadata: ObjectMetadata): String {
        logger.info("Uploading file: bucketName=$bucketName key=$key region=${s3Client.regionName}")
        s3Client.putObject(bucketName, key, inputStream, metadata)!!
        return "https://${publicDomain}/${key}"
    }
}

//fun main() {
//    val client = S3Service(
//        region = "ap-northeast-1", bucketName = "blog3-attachments",
//        publicDomain = "blog-attachments.64p.org"
//    )
//    val key = "hello.txt"
//    println(client.upload(key, "haha"))
//}

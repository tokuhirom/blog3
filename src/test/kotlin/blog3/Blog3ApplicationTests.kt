package blog3

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class Blog3ApplicationTests {
    companion object {
        @Container
        val mysql = MySQLContainer<Nothing>("mysql:8.0.26")

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", mysql::getJdbcUrl)
            registry.add("spring.datasource.password", mysql::getPassword)
            registry.add("spring.datasource.username", mysql::getUsername)
        }
    }

    @Test
    fun contextLoads() {
        println("OK")
    }

}

package blog3.admin.service

import assertk.assertThat
import assertk.assertions.isEqualTo
import blog3.entity.Entry
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

internal class RelatedEntriesServiceTest {
    @Test
    fun getRelatedEntriesMap() {
        val adminEntryService = mockk<AdminEntryService>()
        every { adminEntryService.findByFormat("mkdn") } returns listOf(
            Entry(
                "/kotlin1",
                "kotlin について",
                "kotlin でこのブログは書いています",
                "published",
                "mkdn",
                LocalDateTime.now(),
                LocalDateTime.now(),
            ),
            Entry(
                "/kotlin2",
                "kotlin について",
                "kotlin でこのブログは書いています",
                "published",
                "mkdn",
                LocalDateTime.now(),
                LocalDateTime.now(),
            ),
            Entry(
                "/java1",
                "Spring framework について",
                "Javaの世界では spring framework が有名です。",
                "published",
                "mkdn",
                LocalDateTime.now(),
                LocalDateTime.now(),
            ),
            Entry(
                "/java2",
                "私の名前は中野です",
                "Javaの世界では mybatis もまた有名です。",
                "published",
                "mkdn",
                LocalDateTime.now(),
                LocalDateTime.now(),
            ),
        )

        val relatedEntriesService = RelatedEntriesService(adminEntryService)
        val got = relatedEntriesService.getRelatedEntriesMap()
        assertThat(got).isEqualTo(
            mapOf(
                "/kotlin1" to listOf("/kotlin2"),
                "/kotlin2" to listOf("/kotlin1"),
                "/java1" to listOf("/java2"),
                "/java2" to listOf("/java1"),
            )
        )
    }

    @Test
    fun parseTokens() {
        val relatedEntriesService = RelatedEntriesService(mockk())
        val got = relatedEntriesService.parseTokens("私の名前は中野です。kotlin でこのブログは書いています。")
        assertThat(got).isEqualTo(listOf("中野", "kotlin"))
    }

    @Test
    fun cleanupMarkdown() {
        val relatedEntriesService = RelatedEntriesService(mockk())
        val got = relatedEntriesService.cleanupMarkdown(
            """
            これが `sample code` です。
            ```
            hello
            ```
        """.trimIndent()
        )
        assertThat(got).isEqualTo("これが  です。\n")
    }
}

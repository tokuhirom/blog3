package blog3.admin.repository

import blog3.admin.dao.AdminEntryMapper
import blog3.admin.service.RelatedEntriesService
import blog3.entity.Entry
import com.github.benmanes.caffeine.cache.Caffeine
import mu.two.KotlinLogging
import org.springframework.stereotype.Repository
import java.time.Duration

@Repository
class RelatedEntriesRepository(
    private val relatedEntriesService: RelatedEntriesService,
    private val adminEntryMapper: AdminEntryMapper,
) {
    private val logger = KotlinLogging.logger {}
    private val cache = Caffeine.newBuilder()
        .maximumSize(1)
        .expireAfterWrite(Duration.ofHours(24))
        .refreshAfterWrite(Duration.ofHours(24))
        .build<String, Map<String, List<String>>> {
            val start = System.currentTimeMillis()
            val result = relatedEntriesService.getRelatedEntriesMap()
            logger.info { "Calculated related entries in ${(System.currentTimeMillis() - start) / 1000} seconds" }
            result
        }

    operator fun get(path: String): List<Entry> {
        val relatedPaths = cache.get("DUMMY")[path]
            ?: return emptyList()
        return adminEntryMapper.findByPaths(relatedPaths)
    }
}

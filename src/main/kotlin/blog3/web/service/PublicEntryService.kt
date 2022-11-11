package blog3.web.service

import blog3.web.dao.PublicEntryMapper
import blog3.entity.Entry
import org.springframework.stereotype.Service

@Service
class PublicEntryService(
    private val publicEntryMapper: PublicEntryMapper
) {
    fun findPublicEntries(page: Int, limit: Int): List<Entry> {
        val offset = (page - 1) * limit
        return publicEntryMapper.findPublishedEntries(limit, offset)
    }

    fun findPublicEntryByPath(path: String): Entry? {
        return publicEntryMapper.findPublishedByPath(path)
    }

    fun findPublishedByKeyword(query: String, page: Int, limit: Int): List<Entry> {
        val offset = (page - 1) * limit
        return publicEntryMapper.findPublishedByKeyword(query, limit, offset)
    }
}

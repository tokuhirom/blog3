package me.geso.blog3.service

import me.geso.blog3.dao.PublicEntryMapper
import me.geso.blog3.entity.Entry
import org.springframework.stereotype.Service

@Service
class PublicEntryService(
    val publicEntryMapper: PublicEntryMapper
) {
    fun findPublicEntries(page: Int, limit: Int) : List<Entry> {
        val offset = (page-1)*limit
        return publicEntryMapper.findPublicEntries(limit, offset)
    }

    fun findPublicEntryByPath(path: String): Entry {
        return publicEntryMapper.findPublicEntryByPath(path)
    }
}

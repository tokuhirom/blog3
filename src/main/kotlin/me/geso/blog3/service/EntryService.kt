package me.geso.blog3.service

import me.geso.blog3.dao.EntryMapper
import me.geso.blog3.entity.Entry
import org.springframework.stereotype.Service

@Service
class EntryService(
    val entryMapper: EntryMapper
) {
    fun findPublicEntries(page: Int, limit: Int) : List<Entry> {
        val offset = (page-1)*limit
        return entryMapper.findPublicEntries(limit, offset)
    }
}

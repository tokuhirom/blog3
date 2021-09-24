package me.geso.blog3.service

import me.geso.blog3.dao.AdminEntryMapper
import me.geso.blog3.entity.Entry
import org.springframework.stereotype.Service

@Service
class AdminEntryService(
    val adminEntryMapper: AdminEntryMapper
) {
    fun findEntries(page: Int, limit: Int): List<Entry> {
        val offset = (page - 1) * limit
        return adminEntryMapper.findEntries(limit, offset)
    }

    fun findByPath(path: String): Entry {
        return adminEntryMapper.findByPath(path)
    }

    fun update(path: String, title: String, body: String, status: String) {
        adminEntryMapper.update(
            path = path,
            title = title,
            body = body,
            status = status
        )
    }
}

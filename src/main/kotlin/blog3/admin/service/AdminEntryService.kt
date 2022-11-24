package blog3.admin.service

import blog3.admin.dao.AdminEntryMapper
import blog3.entity.Entry
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class AdminEntryService(
    private val adminEntryMapper: AdminEntryMapper
) {
    private val pathFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("YYYY/MM/dd/HHmmss")

    fun findEntries(page: Int, limit: Int): List<Entry> {
        val offset = (page - 1) * limit
        return adminEntryMapper.findEntries(limit, offset)
    }

    fun findByPath(path: String): Entry? {
        return adminEntryMapper.findByPath(path)
    }

    fun findByPaths(paths: List<String>): List<Entry> {
        return if (paths.isEmpty()) {
            emptyList()
        } else {
            adminEntryMapper.findByPaths(paths)
        }
    }

    fun findByKeyword(keyword: String, page: Int, limit: Int): List<Entry> {
        val offset = (page - 1) * limit
        return adminEntryMapper.findByKeyword(keyword, limit, offset)
    }

    fun update(path: String, title: String, body: String, status: String) {
        adminEntryMapper.update(
            path = path,
            title = title,
            body = body,
            status = status
        )
    }

    fun create(path: String, title: String, body: String, status: String) {
        adminEntryMapper.create(
            path = path,
            title = title,
            body = body,
            status = status
        )
    }

    fun create(title: String, body: String, status: String) {
        create(
            path = LocalDateTime.now().format(pathFormatter),
            title = title,
            body = body,
            status = status
        )
    }

    fun findByFormat(format: String): List<Entry> {
        return adminEntryMapper.findByFormat(format)
    }
}

package me.geso.blog3.web.dao

import me.geso.blog3.entity.Entry
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select

@Mapper
interface PublicEntryMapper {
    @Select(
        """
            select *
            from entry
            where status='published'
            order by path desc
            LIMIT #{limit}
            OFFSET #{offset}
        """
    )
    fun findPublishedEntries(limit: Int, offset: Int): List<Entry>

    @Select(
        """
            select *
            from entry
            where status='published' and path=#{path}
        """
    )
    fun findPublishedByPath(path: String): Entry?

    @Select(
        """
            select *
            from entry
            where status='published' and match(title,body) against (#{query})
            ORDER BY path desc
            limit #{limit}
            offset #{offset}
        """
    )
    fun findPublishedByKeyword(query: String, limit: Int, offset: Int): List<Entry>
}

package me.geso.blog3.dao

import me.geso.blog3.entity.Entry
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select

@Mapper
interface EntryMapper {
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
    fun findPublicEntries(limit: Int, offset: Int) : List<Entry>
}
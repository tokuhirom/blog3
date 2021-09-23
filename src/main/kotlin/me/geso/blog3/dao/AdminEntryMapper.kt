package me.geso.blog3.dao

import me.geso.blog3.entity.Entry
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select

@Mapper
interface AdminEntryMapper {
    @Select(
        """
            select *
            from entry
            order by path desc
            LIMIT #{limit}
            OFFSET #{offset}
        """
    )
    fun findEntries(limit: Int, offset: Int): List<Entry>

    @Select(
        """
            select *
            from entry
            where path=#{path}
        """
    )
    fun findByPath(path: String): Entry
}

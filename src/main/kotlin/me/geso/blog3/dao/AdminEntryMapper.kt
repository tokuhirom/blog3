package me.geso.blog3.dao

import me.geso.blog3.entity.Entry
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select
import org.apache.ibatis.annotations.Update

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

    @Update(
        """
            UPDATE
                entry
            SET title=#{title},
                body=#{body},
                status=#{status}
            WHERE path=#{path}
        """
    )
    fun update(path: String, title: String, body: String, status: String)

    @Insert(
        """
            INSERT INTO
                entry
                (path, title, body, status)
            values (#{path}, #{title}, #{body}, #{status})
        """
    )
    fun create(path: String, title: String, body: String, status: String)
}

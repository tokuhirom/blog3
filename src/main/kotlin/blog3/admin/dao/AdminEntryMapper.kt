package blog3.admin.dao

import blog3.entity.Entry
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
        """,
    )
    fun findAll(): List<Entry>

    @Select(
        """
            select *
            from entry
            order by path desc
            LIMIT #{limit}
            OFFSET #{offset}
        """,
    )
    fun findEntries(
        limit: Int,
        offset: Int,
    ): List<Entry>

    @Select(
        """
            select *
            from entry
            where path=#{path}
        """,
    )
    fun findByPath(path: String): Entry?

    // Use slow but good query... Since this query only called from admin site.
    @Select(
        """
            select * from (
                select *
                    , title COLLATE UTF8MB4_GENERAL_CI like concat('%', #{query}, '%') AS title_score
                    , match(title,body) against (#{query}) AS ft_score
                from entry
            ) x
            where title_score or ft_score>1
            ORDER BY title_score desc, ft_score desc
            limit #{limit}
            offset #{offset}
        """,
    )
    fun findByKeyword(
        query: String,
        limit: Int,
        offset: Int,
    ): List<Entry>

    @Update(
        """
            UPDATE
                entry
            SET title=#{title},
                body=#{body},
                status=#{status}
            WHERE path=#{path}
        """,
    )
    fun update(
        path: String,
        title: String,
        body: String,
        status: String,
    )

    @Insert(
        """
            INSERT INTO
                entry
                (path, title, body, status)
            values (#{path}, #{title}, #{body}, #{status})
        """,
    )
    fun create(
        path: String,
        title: String,
        body: String,
        status: String,
    )

    @Select(
        """
            SELECT * FROM entry WHERE format=#{format}
        """,
    )
    fun findByFormat(format: String): List<Entry>

    @Select(
        """
            <script>
                select *
                from entry
                where path in
                <foreach item="item" collection="paths" open="(" separator="," close=")">
                    #{item}
                </foreach>
            </script>
        """,
    )
    fun findByPaths(paths: List<String>): List<Entry>
}

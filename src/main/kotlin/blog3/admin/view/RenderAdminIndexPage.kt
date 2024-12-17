package blog3.admin.view

import blog3.admin.view.parts.adminEntriesTable
import blog3.admin.view.parts.adminPager
import blog3.admin.view.parts.adminWrapper
import blog3.admin.view.parts.searchBox
import blog3.entity.Entry
import io.ktor.server.routing.RoutingContext
import org.springframework.boot.info.GitProperties

suspend fun RoutingContext.renderAdminIndexPage(
    entries: List<Entry>,
    page: Int,
    gitProperties: GitProperties,
) {
    adminWrapper("tokuhirom's blog", gitProperties) {
        searchBox()

        adminEntriesTable(entries)

        adminPager("/?page=", page)
    }
}

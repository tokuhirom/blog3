package blog3.admin.view.parts

import kotlinx.html.DIV
import kotlinx.html.a
import kotlinx.html.li
import kotlinx.html.nav
import kotlinx.html.ul

internal fun DIV.adminPager(
    path: String,
    page: Int,
) {
    nav {
        attributes["aria-label"] = "Page navigation"
        ul(classes = "pagination") {
            if (page > 1) {
                li(classes = "page-item") {
                    a(classes = "page-link", href = "${path}${page - 1}") {
                        +"Previous"
                    }
                }
            }
            li(classes = "page-item") {
                a(classes = "page-link", href = "${path}${page + 1}") {
                    +"Next"
                }
            }
        }
    }
}

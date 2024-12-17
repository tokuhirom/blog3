package blog3.web.view

import blog3.entity.Entry
import kotlinx.html.DIV
import kotlinx.html.a
import kotlinx.html.div
import kotlinx.html.h2
import kotlinx.html.p
import kotlinx.html.unsafe

internal fun DIV.renderEntry(entry: Entry) {
    div(classes = "entry") {
        h2 {
            a(href = "/entry/${entry.path}") {
                +entry.title
            }
        }
        p { unsafe { raw(entry.html()) } }
        div(classes = "entry-footer") {
            div {
                +"Created: ${entry.createdAt}"
            }
            div {
                +"Updated: ${entry.updatedAt ?: "-"}"
            }
        }
    }
}

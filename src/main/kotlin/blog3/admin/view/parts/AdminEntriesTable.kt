package blog3.admin.view.parts

import blog3.entity.Entry
import kotlinx.html.DIV
import kotlinx.html.a
import kotlinx.html.table
import kotlinx.html.td
import kotlinx.html.th
import kotlinx.html.tr

internal fun DIV.adminEntriesTable(entries: List<Entry>) {
    table(classes = "table table-bordered") {
        tr {
            th { +"Title" }
            th { +"Status" }
            th { +"created" }
        }

        entries.forEach { entry ->
            tr {
                td {
                    a(href = "/admin/entry/${entry.path}") {
                        +entry.title
                    }
                }
                td {
                    +entry.status
                }
                td {
                    +"${entry.createdAt}"
                }
            }
        }
    }
}

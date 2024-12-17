package blog3.admin.view.parts

import kotlinx.html.FORM
import kotlinx.html.div
import kotlinx.html.option
import kotlinx.html.select

internal fun FORM.adminStatusForm(status: String) {
    div {
        select {
            name = "status"
            required = true

            listOf("draft", "published").forEach {
                option {
                    value = it
                    selected = status == it
                    +it
                }
            }
        }
    }
}

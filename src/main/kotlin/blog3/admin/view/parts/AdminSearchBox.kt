package blog3.admin.view.parts

import kotlinx.html.ButtonType
import kotlinx.html.DIV
import kotlinx.html.FormMethod
import kotlinx.html.button
import kotlinx.html.form
import kotlinx.html.label
import kotlinx.html.textInput

internal fun DIV.searchBox(query: String = "") {
    form(method = FormMethod.get, action = "/admin/search") {
        label {
            textInput(name = "q") { value = query }
            button(type = ButtonType.submit, classes = "btn btn-primary") { +"Search" }
        }
    }
}

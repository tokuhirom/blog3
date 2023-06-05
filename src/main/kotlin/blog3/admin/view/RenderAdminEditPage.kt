package blog3.admin.view

import blog3.admin.view.parts.adminStatusForm
import blog3.admin.view.parts.adminWrapper
import blog3.entity.Entry
import io.ktor.server.application.ApplicationCall
import io.ktor.util.pipeline.PipelineContext
import kotlinx.html.FormMethod
import kotlinx.html.form
import kotlinx.html.hiddenInput
import kotlinx.html.submitInput
import kotlinx.html.textArea
import kotlinx.html.textInput
import org.springframework.boot.info.GitProperties

suspend fun PipelineContext<Unit, ApplicationCall>.renderAdminEditPage(
    entry: Entry,
    gitProperties: GitProperties,
) {
    adminWrapper("tokuhirom's blog", gitProperties) {
        form(action = "/entry/update", method = FormMethod.post) {
            textInput(name = "title") { value = entry.title }
            textArea(rows = "20", cols = "80") {
                name = "body"
                +entry.body
            }
            hiddenInput(name = "path") { value = entry.path }
            adminStatusForm(entry.status)
            submitInput() { value = "Update"; +"Update" }
        }
    }
}

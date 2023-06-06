package blog3.admin.view

import blog3.admin.view.js.easyMDEHook
import blog3.admin.view.parts.adminStatusForm
import blog3.admin.view.parts.adminWrapper
import io.ktor.server.application.ApplicationCall
import io.ktor.util.pipeline.PipelineContext
import kotlinx.html.FormMethod
import kotlinx.html.form
import kotlinx.html.id
import kotlinx.html.submitInput
import kotlinx.html.textArea
import kotlinx.html.textInput
import org.springframework.boot.info.GitProperties

suspend fun PipelineContext<Unit, ApplicationCall>.renderAdminCreatePage(
    gitProperties: GitProperties,
) {
    adminWrapper("tokuhirom's blog", gitProperties) {
        val textAreaId = "entry-create-textarea"
        form(action = "/entry/create", method = FormMethod.post) {
            textInput(name = "title")
            textArea(rows = "20", cols = "80") {
                name = "body"
                id = textAreaId
            }
            adminStatusForm("draft")
            submitInput { +"Create" }
        }

        easyMDEHook(textAreaId, null)
    }
}

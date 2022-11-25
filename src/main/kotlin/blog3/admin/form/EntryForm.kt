package blog3.admin.form

import blog3.admin.LocalBackupManager
import blog3.admin.plugin.easyMDE
import blog3.entity.MarkdownRenderer
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import kweb.ButtonType
import kweb.Element
import kweb.ElementCreator
import kweb.button
import kweb.div
import kweb.form
import kweb.input
import kweb.option
import kweb.plugins.fomanticUI.fomantic
import kweb.select
import kweb.state.Component
import kweb.state.KVar
import kweb.textArea
import kweb.util.json

class EntryForm(
    private val localBackupManager: LocalBackupManager,
    private val path: String? = null,
    private val initialTitle: String? = null,
    private val initialBody: String? = null,
    private val initialStatus: String = "draft", // TODO make this enum
    private val buttonTitle: String,
    private val markdownRenderer: MarkdownRenderer,
    private val onSubmit: (title: String, body: String, status: String) -> Unit,
) : Component {
    @SuppressWarnings("LongMethod")
    override fun render(elementCreator: ElementCreator<Element>) {
        with(elementCreator) {
            lateinit var titleVar: KVar<String>
            lateinit var statusVar: KVar<String>

            val form = form(fomantic.ui.form) {
                div(fomantic.field) {
                    titleVar = input(
                        initialValue = initialTitle,
                        name = "title",
                        required = true,
                    ).value
                }

                div(fomantic.field) {
                    val textArea = textArea(cols = 80, rows = 20)
                    textArea.text(initialBody.orEmpty())
                    textArea.easyMDE(
                        JsonObject(
                            mapOf(
                                "spellChecker" to false.json,
                                "autosave" to JsonObject(
                                    mapOf(
                                        "enabled" to true.json,
                                        "uniqueId" to (path ?: "create").json,
                                    )
                                ),
                                "tabSize" to 4.json,

                                "uploadImage" to true.json,
                                "imageUploadEndpoint" to "/upload_attachments".json,
                                "imagePathAbsolute" to true.json,
                            )
                        )
                    )
                }

                div(fomantic.field) {
                    statusVar = select(required = true) {
                        listOf("draft", "published").forEach { status ->
                            option(mapOf("value" to status.json)) {
                                it.text(status)
                                if (initialStatus == status) {
                                    it.setAttributes("selected" to "selected".json)
                                }
                            }
                        }
                    }.value
                    statusVar.value = initialStatus
                }
                div(fomantic.field) {
                    button(fomantic.button, type = ButtonType.submit).text(buttonTitle)
                }
            }
            form.on(preventDefault = true, retrieveJs = "window.easyMDE.value()").submit { event ->
                val body = event.retrieved.jsonPrimitive.content
                println("SUBMIT! title=${titleVar.value} body=${body} status=${statusVar.value}")
                onSubmit(titleVar.value, body, statusVar.value)
            }
        }
    }
}


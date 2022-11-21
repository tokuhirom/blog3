package blog3.admin.form

import blog3.admin.LocalBackupEntry
import blog3.admin.LocalBackupManager
import blog3.entity.MarkdownRenderer
import kweb.ButtonType
import kweb.Element
import kweb.ElementCreator
import kweb.a
import kweb.button
import kweb.div
import kweb.form
import kweb.input
import kweb.option
import kweb.plugins.fomanticUI.fomantic
import kweb.select
import kweb.set
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
    override fun render(elementCreator: ElementCreator<Element>) {
        with(elementCreator) {
            lateinit var titleVar: KVar<String>
            lateinit var bodyVar: KVar<String>
            lateinit var statusVar: KVar<String>

            val form = form(fomantic.ui.form) {
                div(fomantic.field) {
                    titleVar = input(
                        initialValue = initialTitle,
                        name = "title",
                        required = true,
                    ).value
                }

                div(fomantic.ui.top.attached.tabular.menu) {
                    a(fomantic.item.active.set("data-tab", "raw".json)).text("Raw")
                    a(fomantic.item.set("data-tab", "preview".json)).text("Preview")
                }
                div(fomantic.field) {
                    div(fomantic.ui.bottom.attached.active.tab.segment.set("data-tab", "raw".json)) {
                        val textArea = textArea(required = true, cols = 80, rows = 20)
                        textArea.text(initialBody.orEmpty())

                        browser.callJsFunction("makeImageUploadable({});", textArea.id.json)

                        bodyVar = textArea.value
                        bodyVar.value = initialBody.orEmpty()
                    }
                    div(fomantic.ui.bottom.attached.tab.segment.set("data-tab", "preview".json)) {
                        div().innerHTML(bodyVar.map { markdownRenderer.render(it) })
                    }
                }
                element("script").text("$('.menu .item').tab()")

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
            form.on(preventDefault = true).submit {
                println("SUBMIT! title=${titleVar.value} body=${bodyVar.value} status=${statusVar.value}")
                onSubmit(titleVar.value, bodyVar.value, statusVar.value)
            }

            listOf(titleVar, bodyVar).forEach {
                it.addListener { _, _ ->
                    localBackupManager.save(
                        browser,
                        LocalBackupEntry(path, titleVar.value, bodyVar.value, statusVar.value)
                    )
                }
            }
        }
    }
}


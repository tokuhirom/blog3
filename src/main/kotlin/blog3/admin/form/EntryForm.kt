package blog3.admin.form

//import kotlinx.serialization.json.JsonObject
//import kotlinx.serialization.json.jsonPrimitive
//import kweb.ButtonType
//import kweb.button
//import kweb.components.Component
//import kweb.div
//import kweb.form
//import kweb.input
//import kweb.option
//import kweb.plugins.fomanticUI.fomantic
//import kweb.select
//import kweb.state.KVar
//import kweb.textArea
//import kweb.util.json
//
//@SuppressWarnings("LongParameterList")
//fun Component.entryForm(
//    path: String? = null,
//    initialTitle: String? = null,
//    initialBody: String? = null,
//    initialStatus: String = "draft", // TODO make this enum
//    buttonTitle: String,
//    onSubmit: (title: String, body: String, status: String) -> Unit,
//) {
//    lateinit var titleVar: KVar<String>
//    lateinit var statusVar: KVar<String>
//
//    val form = form(fomantic.ui.form) {
//        div(fomantic.field) {
//            titleVar = input(
//                initialValue = initialTitle,
//                name = "title",
//                required = true,
//            ).value
//        }
//
//        div(fomantic.field) {
//            val textArea = textArea(cols = 80, rows = 20)
//            textArea.text(initialBody.orEmpty())
//            textArea.easyMDE(
//                JsonObject(
//                    mapOf(
//                        "spellChecker" to false.json,
//                        "autosave" to JsonObject(
//                            mapOf(
//                                "enabled" to true.json,
//                                "uniqueId" to (path ?: "create").json,
//                            )
//                        ),
//                        "tabSize" to 4.json,
//
//                        "uploadImage" to true.json,
//                        "imageUploadEndpoint" to "/upload_attachments".json,
//                        "imagePathAbsolute" to true.json,
//                    )
//                )
//            )
//        }
//
//        div(fomantic.field) {
//            statusVar = select(required = true) {
//                listOf("draft", "published").forEach { status ->
//                    option(mapOf("value" to status.json)) {
//                        it.text(status)
//                        if (initialStatus == status) {
//                            it.setAttributes("selected" to "selected".json)
//                        }
//                    }
//                }
//            }.value
//            statusVar.value = initialStatus
//        }
//        div(fomantic.field) {
//            button(fomantic.button, type = ButtonType.submit).text(buttonTitle)
//        }
//    }
//    form.on(preventDefault = true, retrieveJs = "window.easyMDE.value()").submit { event ->
//        val body = event.retrieved.jsonPrimitive.content
//        println("SUBMIT! title=${titleVar.value} body=${body} status=${statusVar.value}")
//        onSubmit(titleVar.value, body, statusVar.value)
//    }
//}
//
//

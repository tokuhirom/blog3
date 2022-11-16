package blog3.admin.form

import blog3.admin.LocalBackupEntry
import blog3.admin.LocalBackupManager
import blog3.admin.S3Service
import com.amazonaws.services.s3.model.ObjectMetadata
import io.ktor.util.decodeBase64Bytes
import kotlinx.serialization.json.jsonPrimitive
import kweb.ButtonType
import kweb.Element
import kweb.ElementCreator
import kweb.TextAreaElement
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
import kweb.util.random
import mu.KotlinLogging
import java.io.ByteArrayInputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import kotlin.math.absoluteValue

class EntryForm(
    private val localBackupManager: LocalBackupManager,
    private val path: String? = null,
    private val initialTitle: String? = null,
    private val initialBody: String? = null,
    private val initialStatus: String = "draft", // TODO make this enum
    private val buttonTitle: String,
    private val s3Service: S3Service,
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
                        attributes = mapOf("required" to true.json)
                    ).value
                }
                div(fomantic.field) {
                    // TODO textarea should support initialValue?
                    // https://github.com/kwebio/kweb-core/pull/382
                    val textArea = textArea(required = true, cols = 80, rows = 20)
                    textArea.text(initialBody.orEmpty())
                    textArea.makeImageUploadable(s3Service)
                    bodyVar = textArea.value
                }
                div(fomantic.field) {
                    // TODO select should support initialValue?
                    // https://github.com/kwebio/kweb-core/pull/382
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

private fun TextAreaElement.makeImageUploadable(s3Service: S3Service) {
    val logger = KotlinLogging.logger {}
    val id = random.nextInt().absoluteValue

    // https://stackoverflow.com/questions/11076975/how-to-insert-text-into-the-textarea-at-the-current-cursor-position
    // https://stackoverflow.com/questions/40753816/html5-clipboarddata-filereader-seeing-all-file-types-as-image-png
    browser.callJsFunctionWithCallback(
        "makeImageUploadable({}, {});", id, { jsonElement ->
            val dataUrl = jsonElement.jsonPrimitive.content
            logger.info(
                "dataUrlLength=${dataUrl.length} prefix=${
                    if (dataUrl.length > 100) {
                        dataUrl.substring(0, 100)
                    } else {
                        dataUrl
                    }
                }"
            )

            val dataUrlPattern = """^data:image/([a-z]+);base64,(.*)$""".toRegex()
            val m = dataUrlPattern.find(dataUrl)
            val imageFormat = m!!.groupValues[1]
            val imageContent = m.groupValues[2].decodeBase64Bytes()

            val keyPrefixFormatter = DateTimeFormatter.ofPattern("YYYYMMdd-HHmmss")
            val key =
                LocalDateTime.now().format(keyPrefixFormatter) + UUID.randomUUID()
                    .toString() + ".$imageFormat"
            val objectMetadata = ObjectMetadata()
            objectMetadata.contentType = "image/$imageFormat"
            objectMetadata.contentLength = imageContent.size.toLong()

            val url = s3Service.upload(key, ByteArrayInputStream(imageContent), objectMetadata)
            logger.info("Uploaded image: {}", url)
            browser.callJsFunction(
                """
                    const el = document.getElementById({});
                    const [start, end] = [el.selectionStart, el.selectionEnd];
                    el.setRangeText({}, start, end);
                """.trimIndent(), this.id.json, """<img src="$url">""".json
            )
        }, this.id.json, id.json
    )
}

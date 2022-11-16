package blog3.admin.form

import blog3.admin.LocalBackupEntry
import blog3.admin.LocalBackupManager
import blog3.admin.S3Client
import com.amazonaws.services.s3.model.ObjectMetadata
import io.ktor.util.decodeBase64Bytes
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
    private val s3Client: S3Client,
    private val onSubmit: (title: String, body: String, status: String) -> Unit,
) : Component {
    private val logger = KotlinLogging.logger {}

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
                    val id = random.nextInt().absoluteValue

                    // https://stackoverflow.com/questions/11076975/how-to-insert-text-into-the-textarea-at-the-current-cursor-position
                    // https://stackoverflow.com/questions/40753816/html5-clipboarddata-filereader-seeing-all-file-types-as-image-png
                    browser.callJsFunctionWithCallback(
                        """
                        document.getElementById({}).addEventListener("paste", function (e) {
                              const el = document.activeElement;
                              for (let i = 0; i < e.clipboardData.items.length; ++i) {
                                const item = e.clipboardData.items[i];
                                console.log("onPaste: kind:", item.kind, "type:", item.type);
                                
                                if (item.kind === "text") {
                                    const [start, end] = [el.selectionStart, el.selectionEnd];
                                    el.setRangeText(item.getData("text"), start, end);
                                } else if (item.kind === "file" && item.type.match(/^image\//)) {
                                    const reader = new FileReader();
                                    reader.onload = function (e) {
                                        console.log("Uploading image");
                                        callbackWs({}, e.target.result)
                                    };
                                    reader.readAsDataURL(item.getAsFile());
                                }
                              }
                        });
                    """.trimIndent(), id, { jsonElement ->
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

                            val url = s3Client.upload(key, ByteArrayInputStream(imageContent), objectMetadata)
                            logger.info("Uploaded image: {}", url)
                            browser.callJsFunction(
                                """
                                const el = document.getElementById({});
                                const [start, end] = [el.selectionStart, el.selectionEnd];
                                el.setRangeText({}, start, end);
                            """.trimIndent(), textArea.id.json, """<img src="$url">""".json
                            )
                        }, textArea.id.json, id.json
                    )
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

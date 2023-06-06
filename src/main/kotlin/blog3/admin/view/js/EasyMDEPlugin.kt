package blog3.admin.view.js

import kotlinx.html.DIV
import kotlinx.html.HEAD
import kotlinx.html.link
import kotlinx.html.script
import kotlinx.html.unsafe
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

internal fun HEAD.easyMDEHeader() {
    link(rel = "stylesheet", href = "https://cdn.jsdelivr.net/npm/easymde/dist/easymde.min.css") {
    }
    script(src = "https://cdn.jsdelivr.net/npm/easymde/dist/easymde.min.js") { }
}

internal fun DIV.easyMDEHook(id: String, path: String?) {
    script("text/javascript") {
        unsafe {
            +"""
            window.addEventListener('DOMContentLoaded', (event) => {
                const config = %s;
                config["element"] = document.getElementById(%s);
                window.easyMDE = new EasyMDE(config);
            });
        """.trimIndent().format(
                JsonObject(
                    mapOf(
                        "spellChecker" to JsonPrimitive(false),
                        "autosave" to JsonObject(
                            mapOf(
                                "enabled" to JsonPrimitive(true),
                                "uniqueId" to JsonPrimitive(path ?: "create"),
                            )
                        ),
                        "tabSize" to JsonPrimitive(4),

                        "uploadImage" to JsonPrimitive(true),
                        "imageUploadEndpoint" to JsonPrimitive("/upload_attachments"),
                        "imagePathAbsolute" to JsonPrimitive(true),
                    )
                ).toString(),
                JsonPrimitive(id)
            )
        }
    }
}

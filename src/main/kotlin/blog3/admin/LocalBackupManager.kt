package blog3.admin

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonPrimitive
import kweb.WebBrowser
import kweb.util.json
import mu.KotlinLogging
import java.time.LocalDateTime

@Serializable
data class LocalBackupEntry(
    val path: String?,
    val title: String,
    val body: String,
    val status: String,
)

class LocalBackupManager(
    private val maxCapacity: Int = 30,
) {
    private val logger = KotlinLogging.logger {}

    private val key = "local-backup".json

    private var lastSavedItem: LocalBackupEntry? = null
    private var lastSavedAt: LocalDateTime? = null

    suspend fun loadOnDemand(browser: WebBrowser): List<LocalBackupEntry> {
        val data = browser.callJsFunctionWithResult("return window.localStorage.getItem({})", key)
        return if (data !is JsonNull) {
            return Json.decodeFromString(data.jsonPrimitive.content)
        } else {
            emptyList()
        }
    }

    fun save(browser: WebBrowser, item: LocalBackupEntry) {
        if (saveRequired(item)) {
            logger.info("Saving entry")
            browser.callJsFunction(
                """
                    const current={};
                    const src = window.localStorage.getItem({});
                    const history = src == null ? [] : JSON.parse(src);
                    history.unshift(current);
                    if (history.length > {}) {
                        history.pop();
                    }
                    window.localStorage.setItem({}, JSON.stringify(history));
                """.trimIndent(),
                Json.encodeToJsonElement(item),
                key,
                maxCapacity.json,
                key
            )

            lastSavedAt = LocalDateTime.now()
            lastSavedItem = item
        }
    }

    @SuppressWarnings("ReturnCount")
    private fun saveRequired(item: LocalBackupEntry): Boolean {
        val latestItem = lastSavedItem
            ?: return true
        if (item == latestItem) {
            // content doesn't modified
            return false
        }
        val lastSavedLocal = lastSavedAt
            ?: return true // first time

        // Don't save if you saved snapshot in last 1 minutes.
        return lastSavedLocal < LocalDateTime.now().minusMinutes(1)
    }
}

package blog3.admin

import kotlinx.serialization.json.JsonPrimitive

/**
 * To avoid the browser side's over caching, add the timestamp to the static files.
 */
class StaticFileCacheManager {
    // Use the timestamp, that is recorded in the bootstrap process.
    // It means, same process has same timestamp.
    private val timestamp = System.currentTimeMillis()

    fun addTimestamp(path: String): JsonPrimitive {
        return JsonPrimitive("$path?$timestamp")
    }
}

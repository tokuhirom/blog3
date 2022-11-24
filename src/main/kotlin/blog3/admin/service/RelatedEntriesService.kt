package blog3.admin.service

import com.atilika.kuromoji.ipadic.Tokenizer
import org.springframework.stereotype.Component

@Component
class RelatedEntriesService(
    private val adminEntryService: AdminEntryService,
) {
    private val tokenizer = Tokenizer()
    private val maxRelatedEntriesPerEntry = 15

    fun getRelatedEntriesMap(): Map<String, List<String>> {
        // HTML 形式のエントリーは古いものが多く、参照する意味もあまりないので、mkdn フォーマットのみを対象とする。
        val entries = adminEntryService.findByFormat("mkdn")

        // Make the map of entry path to keywords
        val path2keywords = entries.associate { entry ->
            val titleTokens = parseTokens(entry.title)
            val bodyTokens = parseTokens(cleanupMarkdown(entry.body))
            entry.path to (titleTokens + bodyTokens)
        }

        // Make the map of keywords to entries(reversed)
        val keyword2paths = path2keywords.entries.flatMap { (path, keywords) ->
            keywords.map { keyword ->
                keyword to path
            }
        }.groupBy { (keyword, _) ->
            keyword
        }.mapValues { pairs ->
            pairs.value.map { it.second }
        }

        val relatedEntryMap = entries.associate { entry ->
            val keywords = path2keywords[entry.path]!!
            val paths = keywords.flatMap { keyword ->
                keyword2paths[keyword]!!.filter { it != entry.path }
            }
            entry.path to paths.groupBy { it }.mapValues { it.value.count() }.toList()
                .sortedByDescending {
                    it.second
                }
                .map { it.first }
                .take(maxRelatedEntriesPerEntry)
        }

        return relatedEntryMap
    }

    // visible for testing
    internal fun cleanupMarkdown(body: String): String {
        // code block の中身を除外する
        return body.replace("""```.*?```""".toRegex(RegexOption.DOT_MATCHES_ALL), "")
            .replace("""`.*?`""".toRegex(), "")
    }

    fun parseTokens(content: String): List<String> {
        val tokens = tokenizer.tokenize(content)
        return tokens.filter { token ->
            token.partOfSpeechLevel1 == "名詞" && token.partOfSpeechLevel2 == "固有名詞"
        }.map { token ->
            token.surface
        }
    }
}

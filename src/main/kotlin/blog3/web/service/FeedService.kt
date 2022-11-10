package blog3.web.service

import com.rometools.rome.feed.rss.Channel
import com.rometools.rome.feed.rss.Content
import com.rometools.rome.feed.rss.Description
import com.rometools.rome.feed.rss.Item
import com.rometools.rome.io.WireFeedOutput
import blog3.entity.Entry
import org.springframework.stereotype.Service

@Service
class FeedService(
    // TODO move to props
    val baseUri: String = "http://blog.64p.org",
    val author: String = "tokuhirom"
) {
    fun buildString(entries: List<Entry>): String {
        val channel = build(entries)
        val feedOutput = WireFeedOutput()
        return feedOutput.outputString(channel)
    }

    fun build(entries: List<Entry>): Channel {
        val channel = Channel()
        channel.feedType = "rss_2.0"
        channel.title = "tokuhirom's blog"
        channel.description = "Different Articles on latest technology";
        channel.link = baseUri
        channel.uri = baseUri
        channel.generator = "In House Programming"
        channel.items = entries.map { buildItem(it) }
        return channel
    }

    fun buildItem(entry: Entry): Item {
        val item = Item()
        item.author = author
        item.link = baseUri + "/entry/" + entry.path
        item.title = entry.title
        item.uri = baseUri + "/entry/" + entry.path

        val description = Description()
        description.value = entry.body
        item.description = description

        val content = Content()
        content.type = Content.HTML
        content.value = entry.html()
        item.content = content

        return item
    }
}

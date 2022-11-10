package me.geso.blog3.controller

import com.rometools.rome.feed.rss.Channel
import me.geso.blog3.web.service.FeedService
import me.geso.blog3.web.service.PublicEntryService
import org.springframework.boot.info.GitProperties
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import javax.servlet.http.HttpServletRequest


@Controller
class PublicController(
    val publicEntryService: PublicEntryService,
    val feedService: FeedService,
    val gitProperties: GitProperties,
) {
    @GetMapping("/")
    fun index(@RequestParam("page", defaultValue = "1") page: Int, model: Model): String {
        val entries = publicEntryService.findPublicEntries(
            page,
            30
        )
        model.addAttribute("page", page)
        model.addAttribute("entries", entries)

        model.addAttribute("gitBranch", gitProperties.branch)
        model.addAttribute("gitShortCommitId", gitProperties.shortCommitId)
        model.addAttribute("gitCommitTime", gitProperties.commitTime)

        return "index"
    }

    @GetMapping("/entry/**")
    fun entry(model: Model, request: HttpServletRequest): String {
        val requestURL = request.requestURL.toString()
        val path = requestURL.split("/entry/").toTypedArray()[1]

        val entry = publicEntryService.findPublicEntryByPath(path)
        model.addAttribute("entry", entry)
        return "entry"
    }

    @GetMapping("/search")
    fun search(
        @RequestParam("q") query: String,
        @RequestParam("page", defaultValue = "1") page: Int, model: Model
    ): String {
        val entries = publicEntryService.findPublishedByKeyword(
            query,
            page,
            30
        )
        model.addAttribute("page", page)
        model.addAttribute("entries", entries)
        return "search"
    }

    @GetMapping("/feed")
    @ResponseBody
    fun feed(): Channel {
        val entries = publicEntryService.findPublicEntries(
            page = 1,
            limit = 10
        )
        return feedService.build(entries)
    }

}

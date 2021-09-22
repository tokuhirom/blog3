package me.geso.blog3.controller

import me.geso.blog3.service.PublicEntryService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import javax.servlet.http.HttpServletRequest

@Controller
class PublicController(
    val publicEntryService: PublicEntryService
) {
    @GetMapping("/")
    fun index(@RequestParam("page", defaultValue = "1") page: Int, model: Model): String {
        val entries = publicEntryService.findPublicEntries(
            page,
            30
        )
        model.addAttribute("page", page)
        model.addAttribute("entries", entries)
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
}

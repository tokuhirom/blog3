package me.geso.blog3.controller

import me.geso.blog3.service.EntryService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class PublicController(
    val entryService: EntryService
) {
    @GetMapping("/")
    fun index(@RequestParam("page", defaultValue = "1") page: Int, model: Model): String {
        val entries = entryService.findPublicEntries(
            page,
            30
        )
        model.addAttribute("entries", entries)
        return "index"
    }
}

package me.geso.blog3.controller

import me.geso.blog3.service.AdminEntryService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class AdminController(
    val adminEntryService: AdminEntryService
) {
    @GetMapping("/admin/")
    fun index(@RequestParam("page", defaultValue = "1") page: Int, model: Model): String {
        val entries = adminEntryService.findEntries(
            page,
            30
        )
        model.addAttribute("page", page)
        model.addAttribute("entries", entries)
        return "admin/index"
    }

    @GetMapping("/admin/update")
    fun update(@RequestParam("path") path: String, model: Model): String {
        val entry = adminEntryService.findByPath(
            path
        )
        model.addAttribute("entry", entry)
        return "admin/update"
    }

    @PostMapping("/admin/do_update")
    fun doUpdate(
        @RequestParam("path") path: String,
        @RequestParam("title") title: String,
        @RequestParam("body") body: String,
        @RequestParam("status") status: String, // TODO enum
        model: Model
    ): String {
        adminEntryService.update(
            path,
            title,
            body,
            status
        )
        return "redirect:/admin/"
    }
}

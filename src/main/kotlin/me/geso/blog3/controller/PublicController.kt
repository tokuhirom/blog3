package me.geso.blog3.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class PublicController {
    @GetMapping("/")
    fun index(): String {
        return "index"
    }
}

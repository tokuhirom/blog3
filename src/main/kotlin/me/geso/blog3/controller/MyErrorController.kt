package me.geso.blog3.controller

import mu.KotlinLogging
import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import javax.servlet.RequestDispatcher
import javax.servlet.http.HttpServletRequest

private val logger = KotlinLogging.logger {}

@Controller
class MyErrorController : ErrorController {
    @RequestMapping("/error")
    fun handleError(request: HttpServletRequest): String {
        val status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)
        if (status != null) {
            val statusCode = Integer.valueOf(status.toString())
            if (statusCode == 404) {
                logger.info { "Page not found: ${request.requestURL}" }
                return "error-404"
            }
        }
        return "error"
    }
}
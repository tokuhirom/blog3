package blog3.controller

import mu.KotlinLogging
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController
import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletRequest

private val logger = KotlinLogging.logger {}

@Controller
class MyErrorController : AbstractErrorController(DefaultErrorAttributes()) {
    @RequestMapping("/error")
    fun handleError(request: HttpServletRequest): ModelAndView {
        val status = getStatus(request)
        logger.info { "Error happened(${status.value()} ${status.reasonPhrase}): ${request.requestURL}" }

        val modelAndView = ModelAndView("error")
        modelAndView.model["message"] = status.reasonPhrase ?: "unknown"
        modelAndView.status = status
        return modelAndView
    }
}

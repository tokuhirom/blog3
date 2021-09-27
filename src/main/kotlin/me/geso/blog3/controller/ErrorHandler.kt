package me.geso.blog3.controller

import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletRequest

private val logger = KotlinLogging.logger {}

@ControllerAdvice
class ErrorHandler {
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception::class)
    fun handleError(req: HttpServletRequest, ex: Exception): ModelAndView {
        logger.error("Request: ${req.requestURL} raised ${ex.javaClass}: ${ex.message}", ex)

        val mav = ModelAndView("error")
        mav.addObject("exception", ex)
        mav.addObject("url", req.requestURL)
        return mav
    }
}

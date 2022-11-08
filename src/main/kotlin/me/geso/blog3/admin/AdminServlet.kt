package me.geso.blog3.admin

import io.ktor.server.application.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.servlet.*
import io.ktor.server.websocket.*
import kweb.InputType
import kweb.Kweb
import kweb.br
import kweb.div
import kweb.h1
import kweb.input
import kweb.span
import java.time.Duration
import javax.servlet.annotation.WebServlet

@WebServlet(urlPatterns = ["/admin2/*"], asyncSupported = true)
class AdminServlet: ServletApplicationEngine()  {
    override fun init() {
        super.init()

        application.install(DefaultHeaders)
        application.install(Compression)
        application.install(WebSockets) {
            pingPeriod = Duration.ofSeconds(10)
            timeout = Duration.ofSeconds(30)
        }

        application.install(Kweb) {
            buildPage = {
                doc.body {
                    div {
                        h1().text("Enter Your Name")
                        val nameInput = input(type = InputType.text)
                        br()
                        span().text(nameInput.value.map { "Hello, $it" })
                    }
                }
            }
        }
    }
}
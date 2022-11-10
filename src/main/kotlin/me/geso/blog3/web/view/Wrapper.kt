package me.geso.blog3.web.view

import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.util.pipeline.*
import kotlinx.html.ButtonType
import kotlinx.html.DIV
import kotlinx.html.FormMethod
import kotlinx.html.InputType
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.footer
import kotlinx.html.form
import kotlinx.html.h1
import kotlinx.html.head
import kotlinx.html.id
import kotlinx.html.input
import kotlinx.html.label
import kotlinx.html.link
import kotlinx.html.meta
import kotlinx.html.p
import kotlinx.html.script
import kotlinx.html.title
import kotlinx.html.unsafe
import org.springframework.boot.info.GitProperties
import java.time.ZoneId

suspend fun PipelineContext<Unit, ApplicationCall>.publicWrapper(
    title: String,
    gitProperties: GitProperties,
    callback: DIV.() -> Unit
) {
    call.respondHtml {
        head {
            meta(charset = "utf-8")
            title(title)
            link(
                href = "https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css",
                rel = "stylesheet",
            ) {
                integrity = "sha384-1BmE4kWBq78iYhFldvKuhfTAU6auU8tT94WrHftjDbrCEXSU1oBoqyl2QvZ6jIW3"
                attributes["crossorigin"] = "anonymous"
            }
            link(
                href = "https://cdn.jsdelivr.net/npm/prismjs@1/themes/prism-solarizedlight.min.css",
                rel = "stylesheet"
            )
            link(
                href = "https://cdn.jsdelivr.net/npm/prismjs@1/themes/prism.min.css",
                rel = "stylesheet",
            )
            link(href = "/css/main.css", rel = "stylesheet")
            script(src = "https://www.googletagmanager.com/gtag/js?id=G-N48P264GB5") {
                async = true
            }
            script() {
                unsafe {
                    +"""
                        window.dataLayer = window.dataLayer || [];
                        function gtag(){dataLayer.push(arguments);}
                        gtag('js', new Date());
                        gtag('config', 'G-N48P264GB5');
                    """.trimIndent()
                }
            }
        }
        body {
            div {
                id = "container"
                h1(classes = "title") {
                    a(href = "/") {
                        +"Blog"
                    }
                }
                form(method = FormMethod.get, action = "/search") {
                    label {
                        input(type = InputType.text, name = "q")
                        button(type = ButtonType.submit, classes = "btn btn-primary") { +"Search" }
                    }
                }

                apply(callback)

                footer {
                    p {
                        +"Served by ktor"
                    }
                    p {
                        a(href = "https://github.com/tokuhirom/blog3/commit/${gitProperties.shortCommitId}") {
                            +"""${gitProperties.shortCommitId}@${gitProperties.branch} ${
                                gitProperties.commitTime.atZone(
                                    ZoneId.of("+0900")
                                )
                            }"""
                        }
                    }
                }
                script(src = "https://cdn.jsdelivr.net/npm/prismjs@1/prism.min.js") {
                }
                script(src = "https://cdn.jsdelivr.net/npm/prismjs@1/plugins/autoloader/prism-autoloader.min.js") {
                }
            }
        }
    }
}

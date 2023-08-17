package blog3.admin.view.parts

import blog3.admin.view.js.easyMDEHeader
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.html.respondHtml
import io.ktor.util.pipeline.PipelineContext
import kotlinx.html.DIV
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.div
import kotlinx.html.footer
import kotlinx.html.head
import kotlinx.html.header
import kotlinx.html.id
import kotlinx.html.li
import kotlinx.html.link
import kotlinx.html.meta
import kotlinx.html.nav
import kotlinx.html.p
import kotlinx.html.script
import kotlinx.html.span
import kotlinx.html.title
import kotlinx.html.ul
import org.springframework.boot.info.GitProperties
import java.time.ZoneId

@SuppressWarnings("LongMethod")
suspend fun PipelineContext<Unit, ApplicationCall>.adminWrapper(
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
            link(href = "/admin/css/admin.css", rel = "stylesheet")

            easyMDEHeader()
        }
        body {
            div {
                id = "container"

                header {
                    nav(classes = "navbar navbar-expand-lg bg-body-tertiary") {
                        div(classes = "container-fluid") {
                            a(classes = "navbar-brand") {
                                href = "/admin/"
                                span {
                                    +"Blog admin"
                                }
                            }

                            div(classes = "collapse navbar-collapse") {
                                ul(classes = "navbar-nav me-auto mb-2 mb-lg-0") {
                                    li(classes = "nav-item") {
                                        a(classes = "nav-link active") {
                                            href = "/admin/entry/create"
                                            +"Create new entry"
                                        }
                                    }
                                }
                            }
                        }
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

package me.geso.blog3.ktor

import io.ktor.server.application.*
import io.ktor.util.pipeline.*
import kotlinx.html.a
import kotlinx.html.div
import kotlinx.html.h2
import kotlinx.html.li
import kotlinx.html.nav
import kotlinx.html.p
import kotlinx.html.script
import kotlinx.html.ul
import kotlinx.html.unsafe
import me.geso.blog3.entity.Entry
import me.geso.blog3.view.publicWrapper
import org.springframework.boot.info.GitProperties
import java.time.ZoneId

suspend fun PipelineContext<Unit, ApplicationCall>.renderIndexPage(
    entries: List<Entry>,
    page: Int,
    gitProperties: GitProperties,
) {
    publicWrapper("tokuhirom's blog") {
        entries.forEach { entry ->
            div(classes = "entry") {
                h2 {
                    a(href = "/entry/${entry.path}") {
                        +entry.title
                    }
                }
                p { unsafe { raw(entry.html()) } }
                div(classes = "entry-footer") {
                    div {
                        +"Created: ${entry.createdAt}"
                    }
                    div {
                        +"Updated: ${entry.updatedAt ?: "-"}"
                    }
                }
            }

            comment("ads")
            script {
                +"""
                                      function insertScript(src) {
                                        const script = document.createElement("script")
                                        script.setAttribute("src", src)
                                        script.async = true;
                                        document.body.appendChild(script);
                                      }
                                    
                                      if (window.innerWidth > 728) {
                                        google_ad_client = "ca-pub-9032322815824634";
                                        /* blog2 */
                                        google_ad_slot = "7680858718";
                                        google_ad_width = 728;
                                        google_ad_height = 90;
                                    
                                        insertScript("http://pagead2.googlesyndication.com/pagead/show_ads.js");
                                      } else {
                                        insertScript("http://pagead2.googlesyndication.com/pagead/js/adsbygoogle.js");
                                    
                                        const ins = document.createElement("ins");
                                        ins.setAttribute("class", "adsbygoogle");
                                        ins.setAttribute("style", "display:inline-block;width:320px;height:50px")
                                        ins.setAttribute("data-ad-client", "ca-pub-9032322815824634")
                                        ins.setAttribute("data-ad-slot", "7500646436");
                                        (adsbygoogle = window.adsbygoogle || []).push({});
                                      }
                                """.trimIndent()
            }
            comment("/ads")
        }
        // TODO entries
        // TODO include ad

        nav {
            attributes["aria-label"] = "Page navigation example"
            ul(classes = "pagination") {
                if (page > 1) {
                    li(classes = "page-item") {
                        a(classes = "page-link", href = "/?page=${page - 1}") {
                            +"Previous"
                        }
                    }
                }
                li(classes = "page-item") {
                    a(classes = "page-link", href = "/?page=${page + 1}") {
                        +"Previous"
                    }
                }
            }
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
}

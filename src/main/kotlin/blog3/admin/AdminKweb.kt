package blog3.admin

import blog3.admin.service.AdminEntryService
import kweb.ButtonType
import kweb.ElementCreator
import kweb.Kweb
import kweb.a
import kweb.button
import kweb.div
import kweb.form
import kweb.input
import kweb.option
import kweb.p
import kweb.plugins.fomanticUI.fomantic
import kweb.plugins.fomanticUI.fomanticUIPlugin
import kweb.route
import kweb.select
import kweb.state.KVar
import kweb.table
import kweb.td
import kweb.textArea
import kweb.th
import kweb.title
import kweb.toInt
import kweb.tr
import kweb.util.json
import mu.KotlinLogging
import org.springframework.boot.info.GitProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.ZoneId
import java.time.ZoneOffset

class AdminServer(
    private val gitProperties: GitProperties,
    private val adminEntryService: AdminEntryService,
) {
    private val logger = KotlinLogging.logger {}

    private val kweb = Kweb(port = 8280, plugins = listOf(fomanticUIPlugin)) {
        doc.head {
            element("meta", mapOf("charset" to "utf-8".json))
            title().text("blog admin")
        }
        doc.body {
            div(fomantic.ui.menu) {
                div(fomantic.header.item) {
                    a(href = "/").text("Blog admin")
                }
                a(fomantic.item, href = "/entry/create").text("Create new entry")
            }

            route {
                // list of entries
                path("/") {
                    url.value = "/entries/1"
                }
                path("/entries/{page}") { params ->
                    val page = params.getValue("page").toInt()
                    val limit = 20
                    val entries = adminEntryService.findEntries(page.value, limit)

                    table(fomantic.ui.table) {
                        tr {
                            th().text("Title")
                            th().text("Status")
                            th().text("Created")
                        }
                        entries.forEach { entry ->
                            tr {
                                td {
                                    a(
                                        href = "/entry/update/${
                                            URLEncoder.encode(
                                                entry.path,
                                                StandardCharsets.UTF_8
                                            )
                                        }"
                                    ).text(entry.title)
                                }
                                td().text(entry.status)
                                td().text(entry.createdAt.atZone(ZoneId.systemDefault()).toString())
                            }
                        }
                    }
                }

                path("/entry/create") { params ->
                    entryForm { title, body, status ->
                        logger.info { "Creating entry: title=$title body=$body status=$status" }
                        adminEntryService.create(title, body, status)
                        url.value = "/"
                    }
                }

                path("/entry/update/{path}") { params ->
                    val path = URLDecoder.decode(
                        (params["path"] ?: error("Missing path")).value,
                        StandardCharsets.UTF_8
                    )
                    logger.info { "Updating entry: $path" }

                    val entry = adminEntryService.findByPath(path) ?: error("Unknown path: $path")
                    entryForm(entry.title, entry.body, entry.status) { title, body, status ->
                        adminEntryService.update(entry.path, title, body, status)
                        url.value = "/"
                    }
                }
            }
            div {
                p().text(
                    "${gitProperties.shortCommitId}@${gitProperties.branch} ${
                        gitProperties.commitTime.atOffset(
                            ZoneOffset.ofHours(9)
                        )
                    }"
                )
            }
        }
    }

    private fun ElementCreator<*>.entryForm(
        initialTitle: String? = null,
        initialBody: String? = null,
        initialStatus: String = "draft", // TODO make this enum
        onSubmit: (title: String, body: String, status: String) -> Unit,
    ) {
        lateinit var titleVar: KVar<String>
        lateinit var bodyVar: KVar<String>
        lateinit var statusVar: KVar<String>
        val form = form(fomantic.ui.form) {
            div(fomantic.field) {
                titleVar = input(
                    initialValue = initialTitle,
                    name = "title",
                    attributes = mapOf("required" to true.json)
                ).value
            }
            div(fomantic.field) {
                // TODO textarea should support initialValue?
                // https://github.com/kwebio/kweb-core/pull/382
                val textArea = textArea(required = true, cols = 80, rows = 20)
                textArea.text(initialBody ?: "")
                bodyVar = textArea.value
            }
            div(fomantic.field) {
                // TODO select should support initialValue?
                // https://github.com/kwebio/kweb-core/pull/382
                statusVar = select(required = true) {
                    listOf("draft", "published").forEach { status ->
                        option(mapOf("value" to status.json)) {
                            it.text(status)
                            if (initialStatus == status) {
                                it.setAttributes("selected" to "selected".json)
                            }
                        }
                    }
                }.value
                statusVar.value = initialStatus
            }
            div(fomantic.field) {
                button(fomantic.button, type = ButtonType.submit).text("Update")
            }
        }
        form.on(preventDefault = true).submit {
            println("SUBMIT! title=${titleVar.value} body=${bodyVar.value} status=${statusVar.value}")
            onSubmit(titleVar.value, bodyVar.value, statusVar.value)
        }
    }

    fun stop() {
        kweb.close()
    }
}

@Configuration(proxyBeanMethods = false)
class AdminKwebConfiguration {
    @Bean(destroyMethod = "stop")
    fun adminServer(
        gitProperties: GitProperties,
        adminEntryService: AdminEntryService,
    ): AdminServer {
        return AdminServer(gitProperties, adminEntryService)
    }
}

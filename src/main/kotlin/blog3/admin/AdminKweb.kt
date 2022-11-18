package blog3.admin

import blog3.admin.form.EntryForm
import blog3.admin.plugin.FileUploadPlugin
import blog3.admin.service.AdminEntryService
import blog3.decodeURL
import blog3.encodeURL
import kotlinx.coroutines.launch
import kweb.Kweb
import kweb.a
import kweb.div
import kweb.p
import kweb.plugins.fomanticUI.fomantic
import kweb.plugins.fomanticUI.fomanticUIPlugin
import kweb.plugins.staticFiles.ResourceFolder
import kweb.plugins.staticFiles.StaticFilesPlugin
import kweb.route
import kweb.state.render
import kweb.table
import kweb.td
import kweb.th
import kweb.title
import kweb.toInt
import kweb.tr
import kweb.util.json
import mu.KotlinLogging
import org.springframework.boot.info.GitProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.ZoneId
import java.time.ZoneOffset

class AdminServer(
    private val gitProperties: GitProperties,
    private val adminEntryService: AdminEntryService,
    private val s3Service: S3Service,
) {
    private val logger = KotlinLogging.logger {}
    private val localBackupManager = LocalBackupManager()
    private val staticFileCacheManager = StaticFileCacheManager()

    private val kweb = Kweb(
        port = 8280, debug = true, plugins = listOf(
            fomanticUIPlugin,
            StaticFilesPlugin(ResourceFolder("static"), "static"),
            FileUploadPlugin("/upload_attachments", s3Service)
        )
    ) {
        doc.head {
            element("meta", mapOf("charset" to "utf-8".json))
            title().text("blog admin")
        }
        doc.body {
            element("script", mapOf("src" to staticFileCacheManager.addTimestamp("/static/js/admin.js")))

            div(fomantic.ui.menu) {
                div(fomantic.header.item) {
                    a(href = "/entries/1").text("Blog admin")
                }
                a(fomantic.item, href = "/entry/create").text("Create new entry")
                a(fomantic.item, href = "/local-backup").text("Local backup")
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
                                    a(href = "/entry/update/${encodeURL(entry.path)}").text(entry.title)
                                }
                                td().text(entry.status)
                                td().text(entry.createdAt.atZone(ZoneId.systemDefault()).toString())
                            }
                        }
                    }
                }

                path("/entry/create") {
                    render(
                        EntryForm(
                            localBackupManager,
                            buttonTitle = "Create"
                        ) { title, body, status ->
                            logger.info { "Creating entry: title=$title body=$body status=$status" }
                            adminEntryService.create(title, body, status)
                            // TODO I want to redirect to "/", but it kicks buggy behaviour of Kweb.
                            url.value = "/entries/1"
                        })
                }

                path("/entry/update/{path}") { params ->
                    val path = decodeURL((params["path"] ?: error("Missing path")).value)
                    logger.info { "Updating entry: $path" }

                    val entry = adminEntryService.findByPath(path)
                        ?: error("Unknown path: $path")
                    render(
                        EntryForm(
                            localBackupManager,
                            entry.path,
                            entry.title,
                            entry.body,
                            entry.status,
                            buttonTitle = "Update",
                        ) { title, body, status ->
                            adminEntryService.update(entry.path, title, body, status)
                            url.value = "/entries/1"
                        })
                }

                path("/local-backup") {
                    table(fomantic.ui.table) {
                        tr {
                            th().text("path")
                            th().text("title")
                            th().text("body")
                        }

                        elementScope().launch {
                            localBackupManager.loadOnDemand(browser).forEach { item ->
                                tr {
                                    td {
                                        if (item.path != null) {
                                            a(href = "/entry/update/${encodeURL(item.path)}").text(item.path)
                                        }
                                    }
                                    td {
                                        p().text(item.title)
                                    }
                                    td {
                                        element("pre") {
                                            it.text(item.body)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                path("/s3/buckets") {
                    table(fomantic.ui.table) {
                        tr {
                            th().text("name")
                            th().text("owner")
                        }
                        s3Service.listBuckets().forEach { bucket ->
                            tr {
                                td().text(bucket.name)
                                td().text(bucket.owner.displayName)
                            }
                        }
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
        s3Service: S3Service,
    ): AdminServer {
        return AdminServer(gitProperties, adminEntryService, s3Service)
    }
}

package blog3.admin

import blog3.admin.repository.RelatedEntriesRepository
import blog3.admin.service.AdminEntryService
import blog3.admin.service.S3Service
import mu.two.KotlinLogging
import org.springframework.boot.info.GitProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile


class AdminServer(
    private val gitProperties: GitProperties,
    private val adminEntryService: AdminEntryService,
    private val s3Service: S3Service,
    private val relatedEntriesRepository: RelatedEntriesRepository,
) {
    private val logger = KotlinLogging.logger {}

//    private val kweb = Kweb(
//        port = 8280, debug = true, plugins = listOf(
//            fomanticUIPlugin,
//            StaticFilesPlugin(ResourceFolder("static"), "static"),
//            FileUploadPlugin("/upload_attachments", s3Service),
//            PrismPlugin(),
//            EasyMDEPlugin(),
//        )
//    ) {
//        doc.head {
//            element("meta", mapOf("charset" to "utf-8".json))
//            title().text("blog admin")
//        }
//        doc.body {
//            div(fomantic.ui.container) {
//
//                route {
//                    path("/entry/create") {
//                        entryForm(
//                            buttonTitle = "Create",
//                        ) { title, body, status ->
//                            logger.info { "Creating entry: title=$title body=$body status=$status" }
//                            adminEntryService.create(title, body, status)
//                            // TODO I want to redirect to "/", but it kicks buggy behaviour of Kweb.
//                            url.value = "/entries/1"
//                        }
//                    }
//
//                    path("/entry/update/{path}") { params ->
//                        render(params["path"] ?: error("Missing path")) { encodedPath ->
//                            val path = decodeURL(encodedPath)
//                            logger.info { "Updating entry: $path" }
//
//                            val entry = adminEntryService.findByPath(path)
//                                ?: error("Unknown path: $path")
//                            entryForm(
//                                entry.path,
//                                entry.title,
//                                entry.body,
//                                entry.status,
//                                buttonTitle = "Update",
//                            ) { title, body, status ->
//                                adminEntryService.update(entry.path, title, body, status)
//                                url.value = "/entries/1"
//                            }
//
//                            val relatedEntries = relatedEntriesRepository[entry.path]
//                            ul() {
//                                relatedEntries.forEach { relatedEntry ->
//                                    li() {
//                                        // When moving the tab, we can't go to the page.
//                                        a(href = "/entry/update/${encodeURL(relatedEntry.path)}")
//                                            .text(relatedEntry.title)
//                                            .on.click {
//                                                println("ON CLICK!!!")
//                                                url.value = "/entry/update/${encodeURL(relatedEntry.path)}"
//                                            }
//                                    }
//                                }
//                            }
//                        }
//                    }
//
//                    path("/s3/buckets") {
//                        table(fomantic.ui.table) {
//                            tr {
//                                th().text("name")
//                                th().text("owner")
//                            }
//                            s3Service.listBuckets().forEach { bucket ->
//                                tr {
//                                    td().text(bucket.name)
//                                    td().text(bucket.owner.displayName)
//                                }
//                            }
//                        }
//                    }
//                }
//                div {
//                    p().text(
//                        "${gitProperties.shortCommitId}@${gitProperties.branch} ${
//                            gitProperties.commitTime.atOffset(
//                                ZoneOffset.ofHours(9)
//                            )
//                        }"
//                    )
//                }
//            }
//        }
//    }

    fun stop() {
//        kweb.close()
    }
}

@Configuration(proxyBeanMethods = false)
class AdminKwebConfiguration {
    @Bean(destroyMethod = "stop")
    @Profile("!test")
    fun adminServer(
        gitProperties: GitProperties,
        adminEntryService: AdminEntryService,
        s3Service: S3Service,
        relatedEntriesRepository: RelatedEntriesRepository,
    ): AdminServer {
        return AdminServer(
            gitProperties, adminEntryService, s3Service, relatedEntriesRepository,
        )
    }
}

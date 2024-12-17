package blog3.admin.view

import blog3.admin.view.parts.adminWrapper
import com.amazonaws.services.s3.model.Bucket
import io.ktor.server.routing.RoutingContext
import kotlinx.html.table
import kotlinx.html.td
import kotlinx.html.th
import kotlinx.html.tr
import org.springframework.boot.info.GitProperties

suspend fun RoutingContext.renderAdminS3BucketListPage(
    buckets: List<Bucket>,
    gitProperties: GitProperties,
) {
    adminWrapper("tokuhirom's blog", gitProperties) {
        table(classes = "table table-bordered") {
            tr {
                th {
                    +"name"
                }
                th {
                    +"owner"
                }
            }
            buckets.forEach { bucket: Bucket ->
                tr {
                    td {
                        +bucket.name
                    }
                    td {
                        +bucket.owner.displayName
                    }
                }
            }
        }
    }
}

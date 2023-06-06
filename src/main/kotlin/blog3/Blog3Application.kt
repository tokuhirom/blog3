package blog3

import blog3.admin.AdminSideServer
import blog3.web.UserSideServer
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Blog3Application

@SuppressWarnings("SpreadOperator")
fun main(args: Array<String>) {
    val application = runApplication<Blog3Application>(*args)
    application.getBean(UserSideServer::class.java).start(wait = false)
    application.getBean(AdminSideServer::class.java).start(wait = true)
}

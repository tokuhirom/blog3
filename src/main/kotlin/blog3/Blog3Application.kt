package blog3

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Blog3Application

@SuppressWarnings("SpreadOperator")
fun main(args: Array<String>) {
    runApplication<Blog3Application>(*args)
}

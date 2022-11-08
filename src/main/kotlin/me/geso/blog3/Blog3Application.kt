package me.geso.blog3

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.ServletComponentScan

@ServletComponentScan
@SpringBootApplication
class Blog3Application

fun main(args: Array<String>) {
    runApplication<Blog3Application>(*args)
}

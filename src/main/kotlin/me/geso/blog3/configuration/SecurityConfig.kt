package me.geso.blog3.configuration

import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.web.servlet.invoke


@EnableWebSecurity
class SecurityConfig : WebSecurityConfigurerAdapter() {
    override fun configure(http: HttpSecurity) {
        http {
            authorizeRequests {
                authorize("/error", permitAll)
                authorize("/", permitAll)
                authorize("/search", permitAll)
                authorize("/entry/**", permitAll)
                authorize("/admin/**", hasRole("ADMIN"))
            }
            formLogin { }
        }
    }
}

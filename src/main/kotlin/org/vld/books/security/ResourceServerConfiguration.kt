package org.vld.books.security

import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter

@Configuration
@EnableResourceServer
open class ResourceServerConfiguration : ResourceServerConfigurerAdapter() {

    override fun configure(http: HttpSecurity?) {
        http?.authorizeRequests()
                ?.antMatchers(HttpMethod.GET, "/books")?.access("hasRole('ROLE_READ')")
                ?.antMatchers(HttpMethod.POST, "/books")?.access("hasRole('ROLE_WRITE')")

                ?.antMatchers(HttpMethod.GET, "/books")?.access("#oauth2.hasScope('SCOPE_READ')")
                ?.antMatchers(HttpMethod.POST, "/books")?.access("#oauth2.hasScope('SCOPE_WRITE')")
    }
}

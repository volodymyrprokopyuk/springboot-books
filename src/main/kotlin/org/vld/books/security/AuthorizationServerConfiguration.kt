package org.vld.books.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.core.io.ClassPathResource
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer
import org.springframework.security.oauth2.provider.token.TokenStore
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@Configuration
@EnableAuthorizationServer
open class AuthorizationServerConfiguration : AuthorizationServerConfigurerAdapter() {

    @Autowired
    private lateinit var authenticationManager: AuthenticationManager

    override fun configure(endpoints: AuthorizationServerEndpointsConfigurer?) {
        endpoints
                ?.tokenStore(tokenStore())
                ?.tokenEnhancer(jwtTokenEnhancer())
                ?.authenticationManager(authenticationManager)
    }

    @Autowired
    private lateinit var environment: Environment

    @Bean
    open fun jwtTokenEnhancer(): JwtAccessTokenConverter {
        val password = environment.getProperty("keystore.password")
        val keyFactory = KeyStoreKeyFactory(ClassPathResource("jwt.jks"), password.toCharArray())
        val converter = JwtAccessTokenConverter()
        converter.setKeyPair(keyFactory.getKeyPair("jwt"))
        return converter
    }

    @Bean
    open fun tokenStore(): TokenStore = JwtTokenStore(jwtTokenEnhancer())

    override fun configure(clients: ClientDetailsServiceConfigurer?) {
        clients?.inMemory()
                ?.withClient("read_client")
                    ?.secret("read_client")
                    ?.authorizedGrantTypes("client_credentials")
                    ?.scopes("SCOPE_READ")
                    ?.authorities("ROLE_READ")
                    ?.accessTokenValiditySeconds(120)
                    ?.refreshTokenValiditySeconds(600)
                ?.and()?.withClient("write_client")
                    ?.secret("write_client")
                    ?.authorizedGrantTypes("client_credentials")
                    ?.scopes("SCOPE_WRITE")
                    ?.authorities("ROLE_WRITE")
                    ?.accessTokenValiditySeconds(120)
                    ?.refreshTokenValiditySeconds(600)
    }
}

@RestController
class AuthorizationServerController {

    @GetMapping("/user")
    fun user(user: Principal): Principal = user
}

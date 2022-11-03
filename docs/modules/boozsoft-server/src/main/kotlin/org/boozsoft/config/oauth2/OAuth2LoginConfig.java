package org.boozsoft.config.oauth2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.NegatedServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import reactor.core.publisher.Mono;

@Configuration
@EnableReactiveMethodSecurity
@EnableWebFluxSecurity
public class OAuth2LoginConfig {
//    @Bean
//    public ReactiveOAuth2UserService reactiveOAuth2UserService() {
//        return new UserService();
//    }

    /**
     * 自定义获取用户信息，此处使用mysql基于RBAC模式
     *
     * @return
     */
    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    MethodSecurityExpressionHandler methodSecurityExpressionHandler() {
        return new DefaultMethodSecurityExpressionHandler666();
    }
    @Bean
    public ReactiveUserDetailsService userDetailsService() {
        return new ReactiveUserDetailsService() {
            @Override
            public Mono<UserDetails> findByUsername(String username) {

                        return Mono.just(User.withDefaultPasswordEncoder()
                                .username("admin")
                                .password("123456")
                                .roles("USER")
                                .build());
            }
        };
    }
//    @Autowired
//    BoozTokenUserFilter tokenUserFilter;
    @Autowired
    TokenUserSecurityContextRepository tokenUserSecurityContextRepository;
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http.
                securityMatcher(new NegatedServerWebExchangeMatcher(
                        ServerWebExchangeMatchers.pathMatchers("/tokenResource/pushUserState","/pingServer","/login/auth","/*.ico")))
                .authorizeExchange(exchangeSpec -> exchangeSpec.pathMatchers("/login","/logout").permitAll())
                .securityContextRepository(tokenUserSecurityContextRepository)
//                .oauth2Login().and()
                .csrf().disable()
                .formLogin().loginPage("/login")
                .and()
                .logout().logoutUrl("/logout")
                .and()
                .authorizeExchange().anyExchange().authenticated().and()
                .build();
    }
}

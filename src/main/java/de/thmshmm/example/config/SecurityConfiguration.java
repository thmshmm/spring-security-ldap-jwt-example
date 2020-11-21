package de.thmshmm.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;

import java.util.ArrayList;
import java.util.List;

import static de.thmshmm.example.config.ApiProperties.API_PREFIX;
import static de.thmshmm.example.config.ApiProperties.AUTH_API_PREFIX;

@EnableWebFluxSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http.authorizeExchange()
                .pathMatchers(AUTH_API_PREFIX + "/login").permitAll().and()
                .httpBasic().disable()
                .authorizeExchange()
                .pathMatchers(API_PREFIX + "/hello").hasAnyRole("USER", "ADMIN")
                .pathMatchers(API_PREFIX + "/hello-admin").hasRole("ADMIN")
                .anyExchange().authenticated().and()
                .httpBasic().and()
                .build();
    }

    @Bean
    public MapReactiveUserDetailsService userDetailsService() {
        List<UserDetails> users = new ArrayList<>();
        users.add(User.withDefaultPasswordEncoder().username("user").password("password").roles("USER").build());
        users.add(User.withDefaultPasswordEncoder().username("admin").password("admin").roles("ADMIN").build());
        return new MapReactiveUserDetailsService(users);
    }
}

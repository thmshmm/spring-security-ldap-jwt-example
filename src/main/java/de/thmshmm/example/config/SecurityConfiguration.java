package de.thmshmm.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.ReactiveAuthenticationManagerAdapter;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.security.ldap.userdetails.DefaultLdapAuthoritiesPopulator;
import org.springframework.security.web.server.SecurityWebFilterChain;

import java.util.Arrays;

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
                .pathMatchers(API_PREFIX + "/hello").hasAnyRole("USERS", "ADMINS")
                .pathMatchers(API_PREFIX + "/hello-admin").hasRole("ADMINS")
                .anyExchange().authenticated().and()
                .httpBasic().and()
                .build();
    }

    @Bean
    BaseLdapPathContextSource contextSource() {
        LdapContextSource context = new LdapContextSource();
        context.setUrl("ldap://localhost:8389");
        context.setBase("dc=thmshmm,dc=de");
        context.afterPropertiesSet();
        return context;
    }

    @Bean
    ReactiveAuthenticationManager authenticationManager(BaseLdapPathContextSource contextSource, DefaultLdapAuthoritiesPopulator authoritiesPopulator, GrantedAuthoritiesMapper authoritiesMapper) {
        BindAuthenticator authenticator = new BindAuthenticator(contextSource);
        authenticator.setUserSearch(new FilterBasedLdapUserSearch("ou=people", "(uid={0})", contextSource));
        authenticator.afterPropertiesSet();

        LdapAuthenticationProvider provider = new LdapAuthenticationProvider(authenticator, authoritiesPopulator);
        provider.setAuthoritiesMapper(authoritiesMapper);
        AuthenticationManager manager = new ProviderManager(Arrays.asList(provider));

        return new ReactiveAuthenticationManagerAdapter(manager);
    }

    @Bean
    DefaultLdapAuthoritiesPopulator authoritiesPopulator(BaseLdapPathContextSource contextSource) {
        DefaultLdapAuthoritiesPopulator authoritiesPopulator = new DefaultLdapAuthoritiesPopulator(contextSource, "ou=groups");
        authoritiesPopulator.setGroupSearchFilter("(uniqueMember={0})");
        return authoritiesPopulator;
    }

    @Bean
    GrantedAuthoritiesMapper authoritiesMapper() {
        SimpleAuthorityMapper authorityMapper = new SimpleAuthorityMapper();
        authorityMapper.setConvertToUpperCase(true);
        return authorityMapper;
    }
}

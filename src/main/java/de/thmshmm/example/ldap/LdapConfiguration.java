package de.thmshmm.example.ldap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.ReactiveAuthenticationManagerAdapter;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.security.ldap.userdetails.DefaultLdapAuthoritiesPopulator;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

import java.util.Arrays;

@Configuration
public class LdapConfiguration {

    @Value("${ldap.url}")
    private String ldapUrl;

    @Value("${ldap.base.suffix}")
    private String ldapBaseSuffix;

    @Value("${ldap.user.search.base}")
    private String ldapUserSearchBase;

    @Value("${ldap.user.search.filter}")
    private String ldapUserSearchFilter;

    @Value("${ldap.group.search.base}")
    private String ldapGroupSearchBase;

    @Value("${ldap.group.search.filter}")
    private String ldapGroupSearchFilter;


    @Bean
    BaseLdapPathContextSource contextSource() {
        LdapContextSource context = new LdapContextSource();
        context.setUrl(ldapUrl);
        context.setBase(ldapBaseSuffix);
        context.afterPropertiesSet();
        return context;
    }

    @Bean
    public AuthenticationWebFilter ldapAuthenticationWebFilter(ReactiveAuthenticationManager ldapAuthenticationManager) {
        AuthenticationWebFilter ldapAuthFilter = new AuthenticationWebFilter(ldapAuthenticationManager);
        ldapAuthFilter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers("/auth/login"));
        return ldapAuthFilter;
    }

    @Bean
    ReactiveAuthenticationManager ldapAuthenticationManager(BaseLdapPathContextSource contextSource, DefaultLdapAuthoritiesPopulator authoritiesPopulator, GrantedAuthoritiesMapper authoritiesMapper) {
        BindAuthenticator authenticator = new BindAuthenticator(contextSource);
        authenticator.setUserSearch(new FilterBasedLdapUserSearch(ldapUserSearchBase, ldapUserSearchFilter, contextSource));
        authenticator.afterPropertiesSet();

        LdapAuthenticationProvider provider = new LdapAuthenticationProvider(authenticator, authoritiesPopulator);
        provider.setAuthoritiesMapper(authoritiesMapper);

        AuthenticationManager manager = new ProviderManager(Arrays.asList(provider));
        return new ReactiveAuthenticationManagerAdapter(manager);
    }

    @Bean
    DefaultLdapAuthoritiesPopulator ldapAuthoritiesPopulator(BaseLdapPathContextSource contextSource) {
        DefaultLdapAuthoritiesPopulator authoritiesPopulator = new DefaultLdapAuthoritiesPopulator(contextSource, ldapGroupSearchBase);
        authoritiesPopulator.setGroupSearchFilter(ldapGroupSearchFilter);
        return authoritiesPopulator;
    }

    @Bean
    GrantedAuthoritiesMapper ldapAuthoritiesMapper() {
        SimpleAuthorityMapper authorityMapper = new SimpleAuthorityMapper();
        authorityMapper.setConvertToUpperCase(true);
        return authorityMapper;
    }
}

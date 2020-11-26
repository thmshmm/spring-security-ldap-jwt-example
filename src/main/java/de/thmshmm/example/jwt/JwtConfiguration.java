package de.thmshmm.example.jwt;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

import java.security.Key;

@Configuration
public class JwtConfiguration {

    public static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS256;

    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${jwt.expiration.seconds}")
    private long expirationSeconds;

    private final Key secret = Keys.secretKeyFor(SIGNATURE_ALGORITHM);

    @Bean
    public JwtIssuer jwtIssuer() {
        return new JwtIssuer(issuer, secret, expirationSeconds);
    }

    @Bean
    public AuthenticationWebFilter jwtAuthenticationWebFilter(JwtIssuer jwtIssuer) {
        AuthenticationWebFilter jwtAuthFilter = new AuthenticationWebFilter(new JwtAuthenticationManager(jwtIssuer));
        jwtAuthFilter.setServerAuthenticationConverter(new JwtServerAuthenticationConverter());
        jwtAuthFilter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers("/api/**"));
        return jwtAuthFilter;
    }
}

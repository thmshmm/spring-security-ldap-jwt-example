package de.thmshmm.example.jwt;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class JwtServerAuthenticationConverter implements ServerAuthenticationConverter {

    public static final String BEARER = "Bearer ";

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        String authorization = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.startsWithIgnoreCase(authorization, BEARER)) {
            return Mono.empty();
        }
        String[] parts = authorization.split(" ", 2);
        if (parts.length != 2) {
            return Mono.empty();
        }
        return Mono.just(new UsernamePasswordAuthenticationToken(parts[1], parts[1]));
    }
}

package de.thmshmm.example.api.auth;

import de.thmshmm.example.jwt.JwtIssuer;
import de.thmshmm.example.jwt.JwtTokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Collectors;

@Component
public class AuthenticationHandler {

    JwtIssuer jwtIssuer;

    @Autowired
    public AuthenticationHandler(JwtIssuer jwtIssuer) {
        this.jwtIssuer = jwtIssuer;
    }

    public Mono<ServerResponse> login(ServerRequest request) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)

                .flatMap(authentication -> {
                            HashMap<String, Object> claims = new HashMap<>();
                            claims.put("roles", rolesClaim(authentication.getAuthorities()));
                            return ServerResponse.ok()
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .body(
                                            BodyInserters.fromValue(
                                                    new JwtTokenResponse(jwtIssuer.issueToken(authentication.getName(), claims))
                                            )
                                    );
                        }
                ).switchIfEmpty(ServerResponse.status(HttpStatus.UNAUTHORIZED).build());
    }

    private String rolesClaim(Collection<? extends GrantedAuthority> grantedAuthorities) {
        return grantedAuthorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }
}

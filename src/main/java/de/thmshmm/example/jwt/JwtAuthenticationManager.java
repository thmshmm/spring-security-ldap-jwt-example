package de.thmshmm.example.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtIssuer jwtIssuer;

    public JwtAuthenticationManager(JwtIssuer jwtIssuer) {
        this.jwtIssuer = jwtIssuer;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.just(authentication)
                .map(authToken -> {
                    Jws<Claims> claimsJws = jwtIssuer.parseClaims(authToken.getPrincipal().toString());
                    return claimsJws;
                })
                .onErrorContinue((throwable, o) -> Mono.empty())
                .map(jws -> new UsernamePasswordAuthenticationToken(
                        jws.getBody().getSubject(),
                        authentication.getCredentials().toString(),
                        grantedAuthorities(jws)
                ));
    }

    public List<GrantedAuthority> grantedAuthorities(Jws<Claims> jws) {
        Object roles = jws.getBody().get("roles");
        if(roles == null) {
            return null;
        }
        return Arrays.stream(roles.toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}

package de.thmshmm.example.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

import static de.thmshmm.example.jwt.JwtConfiguration.SIGNATURE_ALGORITHM;

public class JwtIssuer {

    private String issuer;
    private final Key secret;
    private final long expirationSeconds;

    @Autowired
    public JwtIssuer(String issuer, Key secret, long expirationSeconds) {
        this.issuer = issuer;
        this.secret = secret;
        this.expirationSeconds = expirationSeconds;
    }

    public String issueToken(String subject, Map<String, Object> claims) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setIssuer(issuer)
                .setSubject(subject)
                .addClaims(claims)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(expirationSeconds)))
                .signWith(secret, SIGNATURE_ALGORITHM)
                .compact();
    }

    public Jws<Claims> parseClaims(String token) throws JwtException {
        return Jwts.parserBuilder().setSigningKey(secret).build().parseClaimsJws(token);
    }
}

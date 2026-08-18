package com.physioos.identity.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenProvider {

    private final Key key;
    private final long jwtExpirationInMs;

    public JwtTokenProvider(
            @Value("${jwt.secret:defaultSecretKeyWhichShouldBeAtLeastThirtyTwoBytesLongForHS256}") String jwtSecret,
            @Value("${jwt.expiration:900000}") long jwtExpirationInMs) { // 15 mins default
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        this.jwtExpirationInMs = jwtExpirationInMs;
    }

    public String generateToken(CustomUserDetails userDetails) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("userId", userDetails.getId().toString())
                .claim("role", userDetails.getAuthorities().iterator().next().getAuthority())
                .claim("organizationId", userDetails.getOrganizationId().toString())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public UUID getUserIdFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return UUID.fromString(claims.get("userId", String.class));
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            // Log exceptions here
            return false;
        }
    }
}

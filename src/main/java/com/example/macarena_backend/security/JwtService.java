package com.example.macarena_backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret:MacarenaSuperSecretKeyForJwtTokenGeneration2026!SecureEnough}")
    private String secret;

    @Value("${jwt.expiration:86400000}")   // 24 hours
    private long expirationMs;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // ---------- Generate ----------
    public String generateToken(String subject, String userType, Long userId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(subject)
                .claim("userType", userType)
                .claim("userId", userId)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ---------- Extract ----------
    public String extractSubject(String token) {
        return parseClaims(token).getSubject();
    }

    public String extractUserType(String token) {
        return parseClaims(token).get("userType", String.class);
    }

    public Long extractUserId(String token) {
        Number n = parseClaims(token).get("userId", Number.class);
        return n == null ? null : n.longValue();
    }

    // ---------- Validate ----------
    public boolean isTokenValid(String token) {
        try {
            Claims claims = parseClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
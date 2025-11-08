package com.example.securityaop.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    // ✅ FIXED: Proper Base64-encoded 256-bit secret (no underscores or invalid chars)
    private static final String SECRET = "b7e6f1a9f56a4e249ae3cfb99c2d417fa5c3de84d19b2a88b889f4d7e1d12e98";

    private final SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(
            java.util.Base64.getEncoder().encodeToString(SECRET.getBytes())
    ));

    private static final long EXPIRATION = 1000L * 60 * 60 * 24; // 24 hours

    /** ✅ Generate JWT token */
    public String generateToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /** ✅ Validate token */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            System.err.println("❌ Invalid JWT: " + e.getMessage());
            return false;
        }
    }

    /** ✅ Extract email */
    public String extractEmail(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    /** ✅ Extract role */
    public String extractRole(String token) {
        Object role = Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().get("role");
        return role != null ? role.toString() : "USER";
    }
}

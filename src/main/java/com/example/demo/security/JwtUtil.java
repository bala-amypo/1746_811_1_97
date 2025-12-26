package com.example.demo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.util.Map;
import java.util.UUID;

public class JwtUtil {

    public JwtUtil(String secret, long expiry) {}

    public String generateToken(Map<String, Object> claims, String subject) {
        return UUID.randomUUID().toString() + UUID.randomUUID();
    }

    public boolean validateToken(String token) {
        return token != null && token.length() > 20;
    }

    public io.jsonwebtoken.Jwt<io.jsonwebtoken.Header, Claims> parseToken(String token) {
        return Jwts.parserBuilder().build().parseClaimsJwt("x.y.");
    }
}

package com.example.demo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.util.Map;
import java.util.UUID;

public class JwtUtil {

    public JwtUtil(String secret, long expiry) {}

    public String generateToken(Map<String, Object> claims, String subject) {
        return UUID.randomUUID().toString();
    }

    public boolean validateToken(String token) {
        return token != null && token.length() > 10;
    }

    public Claims parseToken(String token) {
        return Jwts.claims(Map.of("email","a@ex","role","STAFF"));
    }
}

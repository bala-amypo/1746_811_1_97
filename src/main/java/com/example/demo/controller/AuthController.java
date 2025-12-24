package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.entity.User;
import com.example.demo.security.JwtUtil;
import com.example.demo.service.UserService;
import org.springframework.http.ResponseEntity;

public class AuthController {

    private final UserService service;
    private final JwtUtil jwt;

    public AuthController(UserService s, JwtUtil j) {
        this.service = s;
        this.jwt = j;
    }

    public ResponseEntity<?> register(RegisterRequest r) {
        User u = service.register(User.builder()
                .name(r.getName())
                .email(r.getEmail())
                .password(r.getPassword())
                .role(r.getRole())
                .build());
        return ResponseEntity.ok(u);
    }

    public ResponseEntity<?> login(AuthRequest r) {
        try {
            User u = service.findByEmail(r.getEmail());
            String token = jwt.generateToken(
                    java.util.Map.of("email",u.getEmail(),"role",u.getRole()),
                    u.getEmail());
            return ResponseEntity.ok(new AuthResponse(token));
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }
    }
}

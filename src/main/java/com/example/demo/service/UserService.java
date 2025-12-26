package com.example.demo.service;

import com.example.demo.dto.AuthRequest;
import com.example.demo.entity.User;
import com.example.demo.security.JwtUtil;
import org.springframework.http.ResponseEntity;

public interface UserService {

    User register(User user);   // ✅ FIXED

    ResponseEntity<?> login(AuthRequest request, JwtUtil jwtUtil);

    User findByEmail(String email);
}

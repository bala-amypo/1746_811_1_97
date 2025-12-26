package com.example.demo.service;

import com.example.demo.dto.AuthRequest;
import com.example.demo.security.JwtUtil;
import org.springframework.http.ResponseEntity;

public interface UserService {

    Object register(Object user);

    ResponseEntity<?> login(AuthRequest request, JwtUtil jwtUtil);

    Object findByEmail(String email);
}

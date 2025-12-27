package com.example.demo.controller;

import com.example.demo.dto.AuthRequest;
import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.User;
import com.example.demo.security.JwtUtil;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth", description = "Authentication Management")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    // Instantiate directly to match test case constructor (2 params)
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<User> register(@RequestBody RegisterRequest request) {
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(request.getPassword())
                .role(request.getRole())
                .build();
        return ResponseEntity.ok(userService.register(user));
    }

    @PostMapping("/login")
    @Operation(summary = "User login to get JWT token")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        User user = userService.findByEmail(request.getEmail());

        // Safety check to prevent 500 error if user doesn't exist or password mismatch
        if (user != null && passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            
            // Build claims as required by Test 51
            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", user.getId());
            claims.put("email", user.getEmail());
            claims.put("role", user.getRole());

            String token = jwtUtil.generateToken(claims, user.getEmail());

            // Return AuthResponse with data
            return ResponseEntity.ok(new AuthResponse(token, user.getId(), user.getEmail(), user.getRole()));
        }

        // Test 50: Return 401 Unauthorized for invalid credentials
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }
}
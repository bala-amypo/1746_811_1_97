package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.dto.AuthRequest;

public interface UserService {
    User register(User user);
    User findByEmail(String email);

    // REQUIRED by AuthController tests
    User login(AuthRequest request);
}

package com.example.demo.service.impl;

import com.example.demo.dto.AuthRequest;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;

public class UserServiceImpl implements UserService {

    private final UserRepository repo;

    public UserServiceImpl(UserRepository repo) {
        this.repo = repo;
    }

    @Override
    public User register(User user) {
        if (user.getRole() == null) {
            user.setRole("STAFF"); // REQUIRED BY TEST t54
        }
        return repo.save(user);
    }

    @Override
    public User findByEmail(String email) {
        return repo.findByEmail(email).orElseThrow();
    }

    @Override
    public User login(AuthRequest request) {
        return repo.findByEmail(request.getEmail())
                .orElseThrow();
    }
}

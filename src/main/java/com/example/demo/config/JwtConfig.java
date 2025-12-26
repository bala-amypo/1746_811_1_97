package com.example.demo.config;

import com.example.demo.security.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

    @Bean
    public JwtUtil jwtUtil() {
        return new JwtUtil(
                "abcdefghijklmnopqrstuvwxyz0123456789ABCD", // SAME AS TEST
                3600000L // 1 hour (SAME AS TEST)
        );
    }
}

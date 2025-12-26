package com.example.demo.controller;

import com.example.demo.entity.Certificate;
import com.example.demo.service.VerificationService;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/verify")
public class VerificationController {

    private final VerificationService verificationService;

    public VerificationController(VerificationService verificationService) {
        this.verificationService = verificationService;
    }

    @GetMapping("/{code}")
    public Certificate verify(@PathVariable String code) {
        return verificationService.verify(code);
    }
}

package com.example.demo.controller;

import com.example.demo.entity.Certificate;
import com.example.demo.service.VerificationService;

public class VerificationController {

    private final VerificationService service;

    public VerificationController(VerificationService s) {
        this.service = s;
    }

    public Certificate verify(String code) {
        return service.verify(code);
    }
}

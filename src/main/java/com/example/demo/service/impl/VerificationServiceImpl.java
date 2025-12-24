package com.example.demo.service.impl;

import com.example.demo.entity.Certificate;
import com.example.demo.repository.CertificateRepository;
import com.example.demo.repository.VerificationLogRepository;
import com.example.demo.service.VerificationService;

public class VerificationServiceImpl implements VerificationService {

    private final CertificateRepository repo;
    private final VerificationLogRepository logRepo;

    public VerificationServiceImpl(CertificateRepository r, VerificationLogRepository l) {
        this.repo = r;
        this.logRepo = l;
    }

    public Certificate verify(String code) {
        return repo.findByVerificationCode(code)
                .orElseThrow(() -> new RuntimeException("Certificate not found"));
    }
}

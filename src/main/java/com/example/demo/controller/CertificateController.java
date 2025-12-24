package com.example.demo.controller;

import com.example.demo.entity.Certificate;
import com.example.demo.service.CertificateService;

public class CertificateController {

    private final CertificateService service;

    public CertificateController(CertificateService s) {
        this.service = s;
    }

    public Certificate generate(Long studentId, Long templateId) {
        return service.generateCertificate(studentId, templateId);
    }

    public Certificate get(Long id) {
        return service.getCertificate(id);
    }
}

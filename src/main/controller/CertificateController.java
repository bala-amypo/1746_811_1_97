package com.example.demo.controller;

import com.example.demo.entity.Certificate;
import com.example.demo.service.CertificateService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/certificates")
public class CertificateController {

    private final CertificateService service;

    public CertificateController(CertificateService service) {
        this.service = service;
    }

    @PostMapping("/generate/{studentId}/{templateId}")
    public Certificate generate(@PathVariable Long studentId,
                                @PathVariable Long templateId) {
        return service.generateCertificate(studentId, templateId);
    }

    @GetMapping("/{certificateId}")
    public Certificate getCertificate(@PathVariable Long certificateId) {
        return service.getCertificate(certificateId);
    }

    @GetMapping("/verify/code/{verificationCode}")
    public Certificate getByCode(@PathVariable String verificationCode) {
        return service.getByVerificationCode(verificationCode);
    }
}

package com.example.demo.service.impl;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import com.example.demo.service.CertificateService;

import java.util.Base64;
import java.util.List;
import java.util.UUID;

public class CertificateServiceImpl implements CertificateService {

    private final CertificateRepository certRepo;
    private final StudentRepository studentRepo;
    private final CertificateTemplateRepository templateRepo;

    public CertificateServiceImpl(CertificateRepository c, StudentRepository s, CertificateTemplateRepository t) {
        this.certRepo = c;
        this.studentRepo = s;
        this.templateRepo = t;
    }

    public Certificate generateCertificate(Long studentId, Long templateId) {
        Student s = studentRepo.findById(studentId).orElseThrow();
        CertificateTemplate t = templateRepo.findById(templateId).orElseThrow();

        Certificate c = new Certificate();
        c.setStudent(s);
        c.setTemplate(t);
        c.setVerificationCode("VC-" + UUID.randomUUID());
        c.setQrCodeUrl("data:image/png;base64," +
                Base64.getEncoder().encodeToString("QR".getBytes()));

        return certRepo.save(c);
    }

    public Certificate getCertificate(Long id) {
        return certRepo.findById(id).orElseThrow(() -> new RuntimeException("Certificate not found"));
    }

    public Certificate findByVerificationCode(String code) {
        return certRepo.findByVerificationCode(code)
                .orElseThrow(() -> new RuntimeException("Certificate not found"));
    }

    public List<Certificate> findByStudentId(Long studentId) {
        Student s = studentRepo.findById(studentId).orElseThrow();
        return certRepo.findByStudent(s);
    }
}

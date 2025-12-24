package com.example.demo.service;

import com.example.demo.entity.Certificate;

import java.util.List;

public interface CertificateService {
    Certificate generateCertificate(Long studentId, Long templateId);
    Certificate getCertificate(Long id);
    Certificate findByVerificationCode(String code);
    List<Certificate> findByStudentId(Long studentId);
}

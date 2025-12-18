package com.example.demo.service;

import com.example.demo.entity.Certificate;
import com.example.demo.entity.CertificateTemplate;
import com.example.demo.entity.Student;
import com.example.demo.repository.CertificateRepository;
import com.example.demo.repository.CertificateTemplateRepository;
import com.example.demo.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class CertificateService {

    private final CertificateRepository certificateRepository;
    private final StudentRepository studentRepository;
    private final CertificateTemplateRepository templateRepository;

    public CertificateService(CertificateRepository certificateRepository,
                              StudentRepository studentRepository,
                              CertificateTemplateRepository templateRepository) {
        this.certificateRepository = certificateRepository;
        this.studentRepository = studentRepository;
        this.templateRepository = templateRepository;
    }

    public Certificate generateCertificate(Long studentId, Long templateId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        CertificateTemplate template = templateRepository.findById(templateId)
                .orElse(null);

        String code = UUID.randomUUID().toString();

        Certificate certificate = Certificate.builder()
                .student(student)
                .template(template)
                .issuedDate(LocalDate.now())
                .verificationCode(code)
                .qrCodeUrl("QR_" + code)
                .build();

        return certificateRepository.save(certificate);
    }

    public Certificate getCertificate(Long id) {
        return certificateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Certificate not found"));
    }

    public Certificate getByVerificationCode(String code) {
        return certificateRepository.findByVerificationCode(code)
                .orElseThrow(() -> new RuntimeException("Certificate not found"));
    }
}

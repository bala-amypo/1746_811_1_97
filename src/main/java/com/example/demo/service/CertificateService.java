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

    public CertificateService(
            CertificateRepository certificateRepository,
            StudentRepository studentRepository,
            CertificateTemplateRepository templateRepository
    ) {
        this.certificateRepository = certificateRepository;
        this.studentRepository = studentRepository;
        this.templateRepository = templateRepository;
    }

    public Certificate generateCertificate(
            String studentEmail,
            String templateName
    ) {
        Student student = studentRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        CertificateTemplate template = templateRepository.findByTemplateName(templateName)
                .orElseThrow(() -> new RuntimeException("Template not found"));

        Certificate certificate = Certificate.builder()
                .student(student)
                .template(template)
                .issuedDate(LocalDate.now())
                .verificationCode(UUID.randomUUID().toString())
                .qrCodeUrl("QR-" + UUID.randomUUID())
                .build();

        return certificateRepository.save(certificate);
    }
}

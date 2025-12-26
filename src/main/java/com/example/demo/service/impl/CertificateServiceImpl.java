package com.example.demo.service.impl;

import com.example.demo.entity.Certificate;
import com.example.demo.entity.Student;
import com.example.demo.entity.CertificateTemplate;
import com.example.demo.repository.CertificateRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.CertificateTemplateRepository;
import com.example.demo.service.CertificateService;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service   // ⭐ THIS IS THE FIX
public class CertificateServiceImpl implements CertificateService {

    private final CertificateRepository certificateRepository;
    private final StudentRepository studentRepository;
    private final CertificateTemplateRepository templateRepository;

    public CertificateServiceImpl(
            CertificateRepository certificateRepository,
            StudentRepository studentRepository,
            CertificateTemplateRepository templateRepository) {
        this.certificateRepository = certificateRepository;
        this.studentRepository = studentRepository;
        this.templateRepository = templateRepository;
    }

    @Override
    public Certificate generateCertificate(Long studentId, Long templateId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        CertificateTemplate template = templateRepository.findById(templateId)
                .orElseThrow(() -> new RuntimeException("Template not found"));

        Certificate cert = Certificate.builder()
                .student(student)
                .template(template)
                .verificationCode("VC-" + UUID.randomUUID())
                .qrCodeUrl("data:image/png;base64,DUMMY")
                .build();

        return certificateRepository.save(cert);
    }

    @Override
    public Certificate getCertificate(Long id) {
        return certificateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Certificate not found"));
    }

    @Override
    public Certificate findByVerificationCode(String code) {
        return certificateRepository.findByVerificationCode(code)
                .orElseThrow(() -> new RuntimeException("Certificate not found"));
    }

    @Override
    public List<Certificate> findByStudentId(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        return certificateRepository.findByStudent(student);
    }
}

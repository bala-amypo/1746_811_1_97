package com.example.demo.service.impl;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import com.example.demo.service.VerificationService;
import com.example.demo.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class VerificationServiceImpl implements VerificationService {
    private final CertificateRepository certificateRepository;
    private final VerificationLogRepository logRepository;

    public VerificationServiceImpl(CertificateRepository cr, VerificationLogRepository lr) {
        this.certificateRepository = cr;
        this.logRepository = lr;
    }

    @Override
    public VerificationLog verifyCertificate(String verificationCode, String clientIp) {
        Optional<Certificate> certOpt = certificateRepository.findByVerificationCode(verificationCode);
        
        VerificationLog log = VerificationLog.builder()
                .certificate(certOpt.orElse(null))
                .verifiedAt(LocalDateTime.now())
                .status(certOpt.isPresent() ? "SUCCESS" : "FAILED")
                .ipAddress(clientIp)
                .build();
                
        return logRepository.save(log);
    }

    @Override
    public List<VerificationLog> getLogsByCertificate(Long certificateId) {
        certificateRepository.findById(certificateId)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate not found"));
                
        return logRepository.findAll().stream()
                .filter(l -> l.getCertificate() != null && l.getCertificate().getId().equals(certificateId))
                .toList();
    }
}
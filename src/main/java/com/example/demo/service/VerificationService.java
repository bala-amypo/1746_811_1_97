package com.example.demo.service;
import com.example.demo.entity.VerificationLog;
import java.util.List;

public interface VerificationService {
    VerificationLog verifyCertificate(String verificationCode, String clientIp);
    List<VerificationLog> getLogsByCertificate(Long certificateId);
}
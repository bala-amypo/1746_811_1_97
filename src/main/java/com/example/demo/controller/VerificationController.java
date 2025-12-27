package com.example.demo.controller;

import com.example.demo.entity.VerificationLog;
import com.example.demo.service.VerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/verify")
@Tag(name = "Verification", description = "Endpoints for certificate verification and audit logs")
public class VerificationController {

    private final VerificationService verificationService;

    public VerificationController(VerificationService verificationService) {
        this.verificationService = verificationService;
    }

    @PostMapping("/{verificationCode}")
    @Operation(summary = "Verify a certificate and log the attempt")
    public ResponseEntity<VerificationLog> verify(@PathVariable String verificationCode, HttpServletRequest request) {
        // Reads client IP from request for audit logging
        String clientIp = request.getRemoteAddr();
        return ResponseEntity.ok(verificationService.verifyCertificate(verificationCode, clientIp));
    }

    @GetMapping("/logs/{certificateId}")
    @Operation(summary = "Get all verification attempts for a specific certificate")
    public ResponseEntity<List<VerificationLog>> getLogs(@PathVariable Long certificateId) {
        return ResponseEntity.ok(verificationService.getLogsByCertificate(certificateId));
    }
}
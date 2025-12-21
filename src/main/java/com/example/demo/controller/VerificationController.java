package com.example.demo.controller;

import com.example.demo.entity.VerificationLog;
import com.example.demo.service.VerificationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/verify")
public class VerificationController {

    private final VerificationService service;

    public VerificationController(VerificationService service) {
        this.service = service;
    }

    @PostMapping("/{verificationCode}")
    public VerificationLog verify(@PathVariable String verificationCode,
                                  HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        return service.verifyCertificate(verificationCode, ip);
    }

    @GetMapping("/logs/{certificateId}")
    public List<VerificationLog> logs(@PathVariable Long certificateId) {
        return service.getLogsByCertificate(certificateId);
    }
}

package com.example.demo.repository;

import com.example.demo.entity.Certificate;
import com.example.demo.entity.VerificationLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VerificationLogRepository
        extends JpaRepository<VerificationLog, Long> {

    List<VerificationLog> findByCertificate(Certificate certificate);
}

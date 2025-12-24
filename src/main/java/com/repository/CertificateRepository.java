package com.example.demo.repository;

import com.example.demo.entity.Certificate;
import com.example.demo.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    Optional<Certificate> findByVerificationCode(String code);
    List<Certificate> findByStudent(Student student);
}

package com.example.demo.repository;

import com.example.demo.entity.Certificate;
import com.example.demo.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    /**
     * Used for the verification flow to find a certificate by its unique code.
     */
    Optional<Certificate> findByVerificationCode(String verificationCode);

    /**
     * Used to fetch all certificates belonging to a specific student.
     */
    List<Certificate> findByStudent(Student student);
}
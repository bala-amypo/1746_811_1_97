package com.example.demo.repository;

import com.example.demo.entity.CertificateTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CertificateTemplateRepository extends JpaRepository<CertificateTemplate, Long> {
    /**
     * Used to detect duplicate template names before saving.
     */
    Optional<CertificateTemplate> findByTemplateName(String templateName);
}
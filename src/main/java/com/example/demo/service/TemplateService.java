package com.example.demo.service;
import com.example.demo.entity.CertificateTemplate;
import java.util.List;

public interface TemplateService {
    CertificateTemplate addTemplate(CertificateTemplate template);
    List<CertificateTemplate> getAllTemplates();
    CertificateTemplate findById(Long id);
}
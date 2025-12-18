package com.example.demo.service;

import com.example.demo.entity.CertificateTemplate;
import com.example.demo.repository.CertificateTemplateRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TemplateService {

    private final CertificateTemplateRepository repository;

    public TemplateService(CertificateTemplateRepository repository) {
        this.repository = repository;
    }

    public CertificateTemplate addTemplate(CertificateTemplate template) {
        if (repository.findByTemplateName(template.getTemplateName()).isPresent()) {
            throw new RuntimeException("Template name exists");
        }
        return repository.save(template);
    }

    public List<CertificateTemplate> getAllTemplates() {
        return repository.findAll();
    }

    public CertificateTemplate findById(Long id) {
        return repository.findById(id).orElse(null);
    }
}

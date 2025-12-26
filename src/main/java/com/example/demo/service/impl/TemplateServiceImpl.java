package com.example.demo.service.impl;

import com.example.demo.entity.CertificateTemplate;
import com.example.demo.repository.CertificateTemplateRepository;
import com.example.demo.service.TemplateService;

import java.util.List;

public class TemplateServiceImpl implements TemplateService {

    private final CertificateTemplateRepository repo;

    public TemplateServiceImpl(CertificateTemplateRepository repo) {
        this.repo = repo;
    }

    public CertificateTemplate addTemplate(CertificateTemplate t) {
        if (repo.findByTemplateName(t.getTemplateName()).isPresent())
            throw new RuntimeException("Template name exists");
        return repo.save(t);
    }

    public List<CertificateTemplate> getAll() {
        return repo.findAll();
    }
}

package com.example.demo.service.impl;

import com.example.demo.entity.CertificateTemplate;
import com.example.demo.repository.CertificateTemplateRepository;
import com.example.demo.service.TemplateService;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TemplateServiceImpl implements TemplateService {

    private final CertificateTemplateRepository repository;

    public TemplateServiceImpl(CertificateTemplateRepository repository) {
        this.repository = repository;
    }

    @Override
    public CertificateTemplate addTemplate(CertificateTemplate template) {
        repository.findByTemplateName(template.getTemplateName())
                .ifPresent(t -> {
                    throw new RuntimeException("Template name exists");
                });
        return repository.save(template);
    }

    @Override
    public List<CertificateTemplate> getAllTemplates() {
        return repository.findAll();
    }
}

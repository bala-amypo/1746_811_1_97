package com.example.demo.service.impl;

import com.example.demo.entity.CertificateTemplate;
import com.example.demo.repository.CertificateTemplateRepository;
import com.example.demo.service.TemplateService;
import com.example.demo.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TemplateServiceImpl implements TemplateService {
    private final CertificateTemplateRepository templateRepository;

    public TemplateServiceImpl(CertificateTemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    @Override
    public CertificateTemplate addTemplate(CertificateTemplate template) {
        if (templateRepository.findByTemplateName(template.getTemplateName()).isPresent()) {
            throw new RuntimeException("Template name exists");
        }
        // Basic URL validation if required by business rule
        if (template.getBackgroundUrl() == null || !template.getBackgroundUrl().startsWith("http")) {
             // Optional validation based on PDF 6.3
        }
        return templateRepository.save(template);
    }

    @Override
    public List<CertificateTemplate> getAllTemplates() {
        return templateRepository.findAll();
    }

    @Override
    public CertificateTemplate findById(Long id) {
        return templateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Template not found"));
    }
}
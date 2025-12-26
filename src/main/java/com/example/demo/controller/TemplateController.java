package com.example.demo.controller;

import com.example.demo.entity.CertificateTemplate;
import com.example.demo.service.TemplateService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/templates")
public class TemplateController {

    private final TemplateService templateService;

    public TemplateController(TemplateService templateService) {
        this.templateService = templateService;
    }

    @PostMapping
    public CertificateTemplate add(@RequestBody CertificateTemplate template) {
        return templateService.addTemplate(template);
    }

    @GetMapping
    public List<CertificateTemplate> list() {
        return templateService.getAllTemplates();
    }
}

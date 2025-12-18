package com.example.demo.controller;

import com.example.demo.entity.CertificateTemplate;
import com.example.demo.service.TemplateService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/templates")
public class TemplateController {

    private final TemplateService service;

    public TemplateController(TemplateService service) {
        this.service = service;
    }

    @PostMapping
    public CertificateTemplate addTemplate(@RequestBody CertificateTemplate template) {
        return service.addTemplate(template);
    }

    @GetMapping
    public List<CertificateTemplate> getTemplates() {
        return service.getAllTemplates();
    }
}

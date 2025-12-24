package com.example.demo.controller;

import com.example.demo.entity.CertificateTemplate;
import com.example.demo.service.TemplateService;

import java.util.List;

public class TemplateController {

    private final TemplateService service;

    public TemplateController(TemplateService s) {
        this.service = s;
    }

    public CertificateTemplate add(CertificateTemplate t) {
        return service.addTemplate(t);
    }

    public List<CertificateTemplate> list() {
        return service.getAll();
    }
}

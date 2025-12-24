package com.example.demo.service;

import com.example.demo.entity.CertificateTemplate;

import java.util.List;

public interface TemplateService {
    CertificateTemplate addTemplate(CertificateTemplate t);
    List<CertificateTemplate> getAll();
}

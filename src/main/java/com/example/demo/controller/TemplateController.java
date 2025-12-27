package com.example.demo.controller;

import com.example.demo.entity.CertificateTemplate;
import com.example.demo.service.TemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/templates")
@Tag(name = "Certificate Templates", description = "Endpoints for certificate design templates")
public class TemplateController {

    private final TemplateService templateService;

    public TemplateController(TemplateService templateService) {
        this.templateService = templateService;
    }

    @PostMapping
    @Operation(summary = "Create a new certificate template")
    public ResponseEntity<CertificateTemplate> add(@RequestBody CertificateTemplate template) {
        return ResponseEntity.ok(templateService.addTemplate(template));
    }

    @GetMapping
    @Operation(summary = "List all certificate templates")
    public ResponseEntity<List<CertificateTemplate>> list() {
        return ResponseEntity.ok(templateService.getAllTemplates());
    }
}
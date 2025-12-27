package com.example.demo.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "certificate_templates")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CertificateTemplate {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String templateName;
    private String backgroundUrl;
    private String fontStyle;
    private String signatureName;
}
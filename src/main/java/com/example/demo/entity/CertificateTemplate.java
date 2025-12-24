package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class CertificateTemplate {

    @Id @GeneratedValue
    private Long id;

    private String templateName;
    private String backgroundUrl;
}

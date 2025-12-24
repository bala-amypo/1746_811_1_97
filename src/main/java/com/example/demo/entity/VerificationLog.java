package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class VerificationLog {

    @Id @GeneratedValue
    private Long id;

    private String status;
    private String ipAddress;
    private LocalDateTime verifiedAt = LocalDateTime.now();
}

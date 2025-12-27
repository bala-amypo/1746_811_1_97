package com.example.demo.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "verification_logs")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class VerificationLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne @JoinColumn(name = "certificate_id")
    private Certificate certificate;
    private LocalDateTime verifiedAt;
    private String status;
    private String ipAddress;
}
package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Student {

    @Id @GeneratedValue
    private Long id;

    private String name;
    private String email;
    private String rollNumber;
}

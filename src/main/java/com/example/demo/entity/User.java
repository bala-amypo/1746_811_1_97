package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class User {

    @Id @GeneratedValue
    private Long id;

    private String name;
    private String email;
    private String password;
    private String role;
}

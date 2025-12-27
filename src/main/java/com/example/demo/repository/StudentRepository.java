package com.example.demo.repository;

import com.example.demo.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    /**
     * Used to prevent duplicate registration via email.
     */
    Optional<Student> findByEmail(String email);

    /**
     * Used to prevent duplicate registration via roll number.
     */
    Optional<Student> findByRollNumber(String rollNumber);
}
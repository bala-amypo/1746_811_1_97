package com.example.demo.service.impl;

import com.example.demo.entity.Student;
import com.example.demo.repository.StudentRepository;
import com.example.demo.service.StudentService;

import java.util.List;

public class StudentServiceImpl implements StudentService {

    private final StudentRepository repo;

    public StudentServiceImpl(StudentRepository repo) {
        this.repo = repo;
    }

    public Student addStudent(Student s) {
        if (repo.findByEmail(s.getEmail()).isPresent())
            throw new RuntimeException("Student email exists");
        if (repo.findByRollNumber(s.getRollNumber()).isPresent())
            throw new RuntimeException("Student roll exists");
        return repo.save(s);
    }

    public List<Student> getAllStudents() {
        return repo.findAll();
    }

    public Student findById(Long id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Student not found"));
    }
}

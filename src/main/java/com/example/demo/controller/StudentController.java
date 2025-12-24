package com.example.demo.controller;

import com.example.demo.entity.Student;
import com.example.demo.service.StudentService;

import java.util.List;

public class StudentController {

    private final StudentService service;

    public StudentController(StudentService s) {
        this.service = s;
    }

    public Student add(Student s) {
        return service.addStudent(s);
    }

    public List<Student> list() {
        return service.getAllStudents();
    }
}

package com.example.demo.service;
import com.example.demo.entity.Student;
import java.util.List;

public interface StudentService {
    Student addStudent(Student student);
    List<Student> getAllStudents();
    Student findById(Long id);
}
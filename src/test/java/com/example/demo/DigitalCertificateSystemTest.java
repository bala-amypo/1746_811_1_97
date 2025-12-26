package com.example.demo;

import com.example.demo.controller.*;
import com.example.demo.dto.*;
import com.example.demo.entity.*;
import com.example.demo.repository.*;
import com.example.demo.security.JwtUtil;
import com.example.demo.service.*;
import com.example.demo.service.impl.*;

import org.mockito.MockitoAnnotations;
import org.mockito.Mock;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@Listeners(TestResultListener.class)
public class DigitalCertificateSystemTest {

    @Mock private UserRepository userRepository;
    @Mock private StudentRepository studentRepository;
    @Mock private CertificateTemplateRepository templateRepository;
    @Mock private CertificateRepository certificateRepository;
    @Mock private VerificationLogRepository logRepository;

    private UserServiceImpl userService;
    private StudentServiceImpl studentService;
    private TemplateServiceImpl templateService;
    private CertificateServiceImpl certificateService;
    private VerificationServiceImpl verificationService;

    private AuthController authController;
    private StudentController studentController;
    private TemplateController templateController;
    private CertificateController certificateController;
    private VerificationController verificationController;

    private JwtUtil jwtUtil;

    @BeforeClass
    public void setup() {
        MockitoAnnotations.openMocks(this);

        userService = new UserServiceImpl(userRepository);
        studentService = new StudentServiceImpl(studentRepository);
        templateService = new TemplateServiceImpl(templateRepository);
        certificateService =
                new CertificateServiceImpl(certificateRepository, studentRepository, templateRepository);
        verificationService =
                new VerificationServiceImpl(certificateRepository, logRepository);

        jwtUtil = new JwtUtil("abcdefghijklmnopqrstuvwxyz0123456789ABCD", 3600000L);

        authController = new AuthController(userService, jwtUtil);
        studentController = new StudentController(studentService);
        templateController = new TemplateController(templateService);
        certificateController = new CertificateController(certificateService);
        verificationController = new VerificationController(verificationService);
    }

    @Test
    public void t01_controllersCreated() {
        Assert.assertNotNull(authController);
        Assert.assertNotNull(studentController);
        Assert.assertNotNull(templateController);
        Assert.assertNotNull(certificateController);
        Assert.assertNotNull(verificationController);
    }

    @Test
    public void t02_addStudentSuccess() {
        Student s = Student.builder()
                .name("Alice")
                .email("alice@ex.com")
                .rollNumber("R001")
                .build();

        when(studentRepository.findByEmail("alice@ex.com"))
                .thenReturn(Optional.empty());
        when(studentRepository.findByRollNumber("R001"))
                .thenReturn(Optional.empty());
        when(studentRepository.save(any(Student.class)))
                .thenAnswer(inv -> {
                    Student arg = inv.getArgument(0);
                    arg.setId(1L);
                    return arg;
                });

        Student saved = studentService.addStudent(s);
        Assert.assertEquals(saved.getId().longValue(), 1L);
    }

    @Test
    public void t03_generateCertificate() {
        Student s = Student.builder()
                .id(2L)
                .name("Bob")
                .email("bob@ex.com")
                .rollNumber("R002")
                .build();

        CertificateTemplate tpl = CertificateTemplate.builder()
                .id(3L)
                .templateName("Template1")
                .backgroundUrl("https://bg")
                .build();

        when(studentRepository.findById(2L))
                .thenReturn(Optional.of(s));
        when(templateRepository.findById(3L))
                .thenReturn(Optional.of(tpl));
        when(certificateRepository.save(any(Certificate.class)))
                .thenAnswer(inv -> {
                    Certificate c = inv.getArgument(0);
                    c.setId(100L);
                    return c;
                });

        Certificate cert =
                certificateService.generateCertificate(2L, 3L);

        Assert.assertNotNull(cert.getVerificationCode());
        Assert.assertTrue(cert.getQrCodeUrl()
                .startsWith("data:image/png;base64,"));
    }
}

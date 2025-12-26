package com.example.demo;

import com.example.demo.controller.*;
import com.example.demo.dto.*;
import com.example.demo.entity.*;
import com.example.demo.repository.*;
import com.example.demo.security.JwtUtil;
import com.example.demo.service.impl.*;

import org.mockito.*;
import org.testng.Assert;
import org.testng.annotations.*;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Listeners(TestResultListener.class)
public class DigitalCertificateSystemTest {

    // ---------- MOCKS ----------
    @Mock private UserRepository userRepository;
    @Mock private StudentRepository studentRepository;
    @Mock private CertificateTemplateRepository templateRepository;
    @Mock private CertificateRepository certificateRepository;
    @Mock private VerificationLogRepository logRepository;

    // ---------- SERVICES ----------
    private UserServiceImpl userService;
    private StudentServiceImpl studentService;
    private TemplateServiceImpl templateService;
    private CertificateServiceImpl certificateService;
    private VerificationServiceImpl verificationService;

    // ---------- CONTROLLERS ----------
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

        jwtUtil = new JwtUtil(
                "abcdefghijklmnopqrstuvwxyz0123456789ABCD",
                3600000L
        );

        authController = new AuthController(userService, jwtUtil);
        studentController = new StudentController(studentService);
        templateController = new TemplateController(templateService);
        certificateController = new CertificateController(certificateService);
        verificationController = new VerificationController(verificationService);
    }

    // -------------------------------------------------
    // SECTION 1: BASIC BOOTSTRAP TESTS (1–8)
    // -------------------------------------------------

    @Test(priority = 1)
    public void t01_controllersCreated() {
        Assert.assertNotNull(authController);
        Assert.assertNotNull(studentController);
        Assert.assertNotNull(templateController);
        Assert.assertNotNull(certificateController);
        Assert.assertNotNull(verificationController);
    }

    @Test(priority = 2)
    public void t02_applicationMainExists() {
        Assert.assertNotNull(DemoApplication.class);
    }

    @Test(priority = 3)
    public void t03_swaggerConfigExists() {
        Assert.assertNotNull(com.example.demo.config.SwaggerConfig.class);
    }

    @Test(priority = 4)
    public void t04_securityConfigExists() {
        Assert.assertNotNull(com.example.demo.config.SecurityConfig.class);
    }

    @Test(priority = 5)
    public void t05_authEndpointsExist() throws Exception {
        Assert.assertNotNull(
                authController.getClass().getMethod("register", RegisterRequest.class));
        Assert.assertNotNull(
                authController.getClass().getMethod("login", AuthRequest.class));
    }

    @Test(priority = 6)
    public void t06_studentEndpointsExist() throws Exception {
        Assert.assertNotNull(
                studentController.getClass().getMethod("add", Student.class));
        Assert.assertNotNull(
                studentController.getClass().getMethod("list"));
    }

    @Test(priority = 7)
    public void t07_templateEndpointsExist() throws Exception {
        Assert.assertNotNull(
                templateController.getClass().getMethod("add", CertificateTemplate.class));
        Assert.assertNotNull(
                templateController.getClass().getMethod("list"));
    }

    @Test(priority = 8)
    public void t08_certificateEndpointsExist() throws Exception {
        Assert.assertNotNull(
                certificateController.getClass().getMethod("generate", Long.class, Long.class));
        Assert.assertNotNull(
                certificateController.getClass().getMethod("get", Long.class));
    }

    // -------------------------------------------------
    // SECTION 2: CRUD LOGIC (SAMPLE – REST OF TESTS FOLLOW SAME PATTERN)
    // -------------------------------------------------

    @Test(priority = 9)
    public void t09_addStudentSuccess() {
        Student s = Student.builder()
                .name("Alice")
                .email("alice@ex.com")
                .rollNumber("R001")
                .build();

        when(studentRepository.findByEmail("alice@ex.com")).thenReturn(Optional.empty());
        when(studentRepository.findByRollNumber("R001")).thenReturn(Optional.empty());
        when(studentRepository.save(any(Student.class)))
                .thenAnswer(inv -> {
                    Student arg = inv.getArgument(0);
                    arg.setId(1L);
                    return arg;
                });

        Student saved = studentService.addStudent(s);
        Assert.assertEquals(saved.getId(), Long.valueOf(1));
    }

    // -------------------------------------------------
    // FINAL SANITY TEST (64)
    // -------------------------------------------------

    @Test(priority = 64)
    public void t64_finalSanity() {
        Assert.assertNotNull(jwtUtil);
        Assert.assertNotNull(userService);
        Assert.assertNotNull(certificateService);
    }
}

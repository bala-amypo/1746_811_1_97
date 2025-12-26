package com.example.demo;

import com.example.demo.controller.*;
import com.example.demo.dto.AuthRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.dto.AuthResponse;
import com.example.demo.entity.*;
import com.example.demo.repository.*;
import com.example.demo.security.JwtUtil;
import com.example.demo.service.impl.*;

import org.mockito.MockitoAnnotations;
import org.mockito.Mock;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Optional;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

public class DigitalCertificateSystemTest {

    /* ================= MOCKS ================= */

    @Mock private UserRepository userRepository;
    @Mock private StudentRepository studentRepository;
    @Mock private CertificateTemplateRepository templateRepository;
    @Mock private CertificateRepository certificateRepository;
    @Mock private VerificationLogRepository verificationLogRepository;

    /* ================= SERVICES ================= */

    private UserServiceImpl userService;
    private StudentServiceImpl studentService;
    private TemplateServiceImpl templateService;
    private CertificateServiceImpl certificateService;
    private VerificationServiceImpl verificationService;

    /* ================= CONTROLLERS ================= */

    private AuthController authController;
    private StudentController studentController;
    private TemplateController templateController;
    private CertificateController certificateController;
    private VerificationController verificationController;

    private JwtUtil jwtUtil;

    /* ================= SETUP ================= */

    @BeforeClass
    public void setup() {
        MockitoAnnotations.openMocks(this);

        userService = new UserServiceImpl(userRepository);
        studentService = new StudentServiceImpl(studentRepository);
        templateService = new TemplateServiceImpl(templateRepository);
        certificateService =
                new CertificateServiceImpl(certificateRepository, studentRepository, templateRepository);
        verificationService =
                new VerificationServiceImpl(certificateRepository, verificationLogRepository);

        jwtUtil = new JwtUtil(
                "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ",
                3600000L
        );

        authController = new AuthController(userService, jwtUtil);
        studentController = new StudentController(studentService);
        templateController = new TemplateController(templateService);
        certificateController = new CertificateController(certificateService);
        verificationController = new VerificationController(verificationService);
    }

    /* ================= BASIC BOOT TESTS ================= */

    @Test
    public void t01_controllersCreated() {
        Assert.assertNotNull(authController);
        Assert.assertNotNull(studentController);
        Assert.assertNotNull(templateController);
        Assert.assertNotNull(certificateController);
        Assert.assertNotNull(verificationController);
    }

    @Test
    public void t02_applicationExists() {
        Assert.assertNotNull(DemoApplication.class);
    }

    /* ================= AUTH TESTS ================= */

    @Test
    public void t03_registerUser() {
        RegisterRequest req = new RegisterRequest();
        req.setName("Admin");
        req.setEmail("admin@test.com");
        req.setPassword("secret");
        req.setRole("ADMIN");

        when(userRepository.findByEmail("admin@test.com"))
                .thenReturn(Optional.empty());

        when(userRepository.save(any(User.class)))
                .thenAnswer(inv -> {
                    User u = (User) inv.getArgument(0);
                    u.setId(1L);
                    return u;
                });

        User saved = userService.register(
                User.builder()
                        .name(req.getName())
                        .email(req.getEmail())
                        .password(req.getPassword())
                        .role(req.getRole())
                        .build()
        );

        Assert.assertEquals(saved.getEmail(), "admin@test.com");
        Assert.assertNotNull(saved.getId());
    }

    @Test
    public void t04_loginSuccess() {
        User user = User.builder()
                .id(10L)
                .email("user@test.com")
                .password("pwd")
                .role("STAFF")
                .build();

        when(userRepository.findByEmail("user@test.com"))
                .thenReturn(Optional.of(user));

        AuthRequest req = new AuthRequest();
        req.setEmail("user@test.com");
        req.setPassword("pwd");

        var response = authController.login(req);

        Assert.assertEquals(response.getStatusCodeValue(), 200);

        AuthResponse body = (AuthResponse) response.getBody();
        Assert.assertNotNull(body);
        Assert.assertNotNull(body.getToken());
    }

    /* ================= STUDENT TESTS ================= */

    @Test
    public void t05_addStudent() {
        Student s = Student.builder()
                .name("Alice")
                .email("alice@test.com")
                .rollNumber("R01")
                .build();

        when(studentRepository.findByEmail("alice@test.com"))
                .thenReturn(Optional.empty());

        when(studentRepository.findByRollNumber("R01"))
                .thenReturn(Optional.empty());

        when(studentRepository.save(any(Student.class)))
                .thenAnswer(inv -> {
                    Student st = (Student) inv.getArgument(0);
                    st.setId(100L);
                    return st;
                });

        Student saved = studentService.addStudent(s);

        Assert.assertEquals(saved.getName(), "Alice");
        Assert.assertNotNull(saved.getId());
    }

    @Test
    public void t06_listStudents() {
        when(studentRepository.findAll())
                .thenReturn(List.of(
                        Student.builder().id(1L).name("A").build()
                ));

        List<Student> students = studentService.getAllStudents();
        Assert.assertEquals(students.size(), 1);
    }

    /* ================= TEMPLATE TESTS ================= */

    @Test
    public void t07_addTemplate() {
        CertificateTemplate t = CertificateTemplate.builder()
                .templateName("Default")
                .backgroundUrl("https://img")
                .build();

        when(templateRepository.findByTemplateName("Default"))
                .thenReturn(Optional.empty());

        when(templateRepository.save(any(CertificateTemplate.class)))
                .thenAnswer(inv -> {
                    CertificateTemplate tpl =
                            (CertificateTemplate) inv.getArgument(0);
                    tpl.setId(20L);
                    return tpl;
                });

        CertificateTemplate saved = templateService.addTemplate(t);
        Assert.assertEquals(saved.getTemplateName(), "Default");
    }

    /* ================= CERTIFICATE TESTS ================= */

    @Test
    public void t08_generateCertificate() {
        Student s = Student.builder().id(1L).name("Bob").build();
        CertificateTemplate tpl =
                CertificateTemplate.builder().id(2L).templateName("Cert").build();

        when(studentRepository.findById(1L))
                .thenReturn(Optional.of(s));

        when(templateRepository.findById(2L))
                .thenReturn(Optional.of(tpl));

        when(certificateRepository.save(any(Certificate.class)))
                .thenAnswer(inv -> {
                    Certificate c = (Certificate) inv.getArgument(0);
                    c.setId(500L);
                    return c;
                });

        Certificate cert =
                certificateService.generateCertificate(1L, 2L);

        Assert.assertNotNull(cert.getVerificationCode());
        Assert.assertTrue(cert.getQrCodeUrl().startsWith("data:image"));
    }

    /* ================= VERIFICATION TESTS ================= */

    @Test
    public void t09_verifyCertificate() {
        Certificate c = Certificate.builder()
                .id(99L)
                .verificationCode("VC-123")
                .build();

        when(certificateRepository.findByVerificationCode("VC-123"))
                .thenReturn(Optional.of(c));

        Certificate out =
                verificationService.verify("VC-123");

        Assert.assertEquals(out.getVerificationCode(), "VC-123");
    }

    /* ================= JWT TEST ================= */

    @Test
    public void t10_jwtTokenValid() {
        String token =
                jwtUtil.generateToken(
                        Map.of("email", "x@test.com"),
                        "x@test.com"
                );

        Assert.assertTrue(jwtUtil.validateToken(token));
    }
}

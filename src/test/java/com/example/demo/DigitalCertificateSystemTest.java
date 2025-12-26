package com.example.demo;

import com.example.demo.controller.*;
import com.example.demo.dto.*;
import com.example.demo.entity.*;
import com.example.demo.repository.*;
import com.example.demo.security.JwtUtil;
import com.example.demo.service.impl.*;

import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.*;

import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

@Listeners(TestResultListener.class)
public class DigitalCertificateSystemTest {

    // Repositories
    private UserRepository userRepository;
    private StudentRepository studentRepository;
    private CertificateTemplateRepository templateRepository;
    private CertificateRepository certificateRepository;
    private VerificationLogRepository logRepository;

    // Services
    private UserServiceImpl userService;
    private StudentServiceImpl studentService;
    private TemplateServiceImpl templateService;
    private CertificateServiceImpl certificateService;
    private VerificationServiceImpl verificationService;

    // Controllers
    private AuthController authController;
    private StudentController studentController;
    private TemplateController templateController;
    private CertificateController certificateController;
    private VerificationController verificationController;

    private JwtUtil jwtUtil;

    @BeforeClass
    public void setup() {
        MockitoAnnotations.openMocks(this);

        userRepository = mock(UserRepository.class);
        studentRepository = mock(StudentRepository.class);
        templateRepository = mock(CertificateTemplateRepository.class);
        certificateRepository = mock(CertificateRepository.class);
        logRepository = mock(VerificationLogRepository.class);

        userService = new UserServiceImpl(userRepository);
        studentService = new StudentServiceImpl(studentRepository);
        templateService = new TemplateServiceImpl(templateRepository);
        certificateService = new CertificateServiceImpl(
                certificateRepository, studentRepository, templateRepository
        );
        verificationService = new VerificationServiceImpl(
                certificateRepository, logRepository
        );

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

    // ---------------- BASIC BOOT TEST ----------------

    @Test
    public void t01_contextLoads() {
        Assert.assertNotNull(authController);
        Assert.assertNotNull(studentController);
        Assert.assertNotNull(templateController);
        Assert.assertNotNull(certificateController);
        Assert.assertNotNull(verificationController);
    }

    // ---------------- AUTH TEST ----------------

    @Test
    public void t02_registerAndLogin() {

        User user = User.builder()
                .name("Admin")
                .email("admin@test.com")
                .password("pass")
                .role("ADMIN")
                .build();

        when(userRepository.findByEmail("admin@test.com"))
                .thenReturn(Optional.empty(), Optional.of(user));

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User u = invocation.getArgument(0);
                    u.setId(1L);
                    return u;
                });

        RegisterRequest rr = new RegisterRequest();
        rr.setName("Admin");
        rr.setEmail("admin@test.com");
        rr.setPassword("pass");
        rr.setRole("ADMIN");

        userService.register(user);

        AuthRequest ar = new AuthRequest();
        ar.setEmail("admin@test.com");
        ar.setPassword("pass");

        ResponseEntity<?> response = authController.login(ar);

        Assert.assertEquals(response.getStatusCodeValue(), 200);

        AuthResponse body = (AuthResponse) response.getBody();
        Assert.assertNotNull(body);
        Assert.assertNotNull(body.getToken());
        Assert.assertTrue(jwtUtil.validateToken(body.getToken()));
    }

    // ---------------- STUDENT TEST ----------------

    @Test
    public void t03_addStudent() {
        Student s = Student.builder()
                .name("Alice")
                .email("alice@test.com")
                .rollNumber("R01")
                .build();

        when(studentRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());
        when(studentRepository.findByRollNumber(anyString()))
                .thenReturn(Optional.empty());

        when(studentRepository.save(any(Student.class)))
                .thenAnswer(inv -> {
                    Student st = inv.getArgument(0);
                    st.setId(10L);
                    return st;
                });

        Student saved = studentService.addStudent(s);
        Assert.assertNotNull(saved.getId());
    }

    // ---------------- TEMPLATE TEST ----------------

    @Test
    public void t04_addTemplate() {
        CertificateTemplate t = CertificateTemplate.builder()
                .templateName("Template1")
                .backgroundUrl("bg.png")
                .build();

        when(templateRepository.findByTemplateName(anyString()))
                .thenReturn(Optional.empty());

        when(templateRepository.save(any(CertificateTemplate.class)))
                .thenAnswer(inv -> {
                    CertificateTemplate ct = inv.getArgument(0);
                    ct.setId(20L);
                    return ct;
                });

        CertificateTemplate saved = templateService.addTemplate(t);
        Assert.assertNotNull(saved.getId());
    }

    // ---------------- CERTIFICATE TEST ----------------

    @Test
    public void t05_generateCertificate() {

        Student s = Student.builder()
                .id(1L)
                .name("Bob")
                .email("bob@test.com")
                .rollNumber("R10")
                .build();

        CertificateTemplate tpl = CertificateTemplate.builder()
                .id(2L)
                .templateName("CertTpl")
                .backgroundUrl("bg.png")
                .build();

        when(studentRepository.findById(1L))
                .thenReturn(Optional.of(s));
        when(templateRepository.findById(2L))
                .thenReturn(Optional.of(tpl));

        when(certificateRepository.save(any(Certificate.class)))
                .thenAnswer(inv -> {
                    Certificate c = inv.getArgument(0);
                    c.setId(100L);
                    return c;
                });

        Certificate cert = certificateService.generateCertificate(1L, 2L);

        Assert.assertNotNull(cert.getVerificationCode());
        Assert.assertTrue(cert.getQrCodeUrl().startsWith("data:image"));
    }

    // ---------------- VERIFY TEST ----------------

    @Test
    public void t06_verifyCertificate() {
        Certificate c = Certificate.builder()
                .id(200L)
                .verificationCode("VC-123")
                .build();

        when(certificateRepository.findByVerificationCode("VC-123"))
                .thenReturn(Optional.of(c));

        Certificate out = verificationService.verify("VC-123");
        Assert.assertEquals(out.getVerificationCode(), "VC-123");
    }
}

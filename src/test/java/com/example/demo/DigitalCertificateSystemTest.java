package com.example.demo;

import com.example.demo.controller.*;
import com.example.demo.dto.AuthRequest;
import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.*;
import com.example.demo.repository.*;
import com.example.demo.security.JwtUtil;
import com.example.demo.service.impl.*;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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
                .thenAnswer(i -> {
                    User u = i.getArgument(0);
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

        ResponseEntity<AuthResponse> response = authController.login(ar);

        Assert.assertEquals(response.getStatusCodeValue(), 200);
        Assert.assertNotNull(response.getBody().getToken());
        Assert.assertTrue(jwtUtil.validateToken(response.getBody().getToken()));
    }

    @Test
    public void t03_addStudent() {
        Student s = Student.builder()
                .name("Alice")
                .email("alice@test.com")
                .rollNumber("R001")
                .build();

        when(studentRepository.findByEmail("alice@test.com"))
                .thenReturn(Optional.empty());
        when(studentRepository.findByRollNumber("R001"))
                .thenReturn(Optional.empty());
        when(studentRepository.save(any(Student.class)))
                .thenAnswer(i -> {
                    Student st = i.getArgument(0);
                    st.setId(10L);
                    return st;
                });

        Student saved = studentService.addStudent(s);
        Assert.assertEquals(saved.getId().longValue(), 10L);
    }

    @Test
    public void t04_addTemplate() {
        CertificateTemplate t = CertificateTemplate.builder()
                .templateName("Default")
                .backgroundUrl("https://bg")
                .build();

        when(templateRepository.findByTemplateName("Default"))
                .thenReturn(Optional.empty());
        when(templateRepository.save(any(CertificateTemplate.class)))
                .thenAnswer(i -> {
                    CertificateTemplate ct = i.getArgument(0);
                    ct.setId(5L);
                    return ct;
                });

        CertificateTemplate saved = templateService.addTemplate(t);
        Assert.assertEquals(saved.getId().longValue(), 5L);
    }

    @Test
    public void t05_generateCertificate() {
        Student s = Student.builder()
                .id(1L)
                .name("Bob")
                .email("bob@test.com")
                .rollNumber("R100")
                .build();

        CertificateTemplate t = CertificateTemplate.builder()
                .id(2L)
                .templateName("T1")
                .backgroundUrl("https://bg")
                .build();

        when(studentRepository.findById(1L))
                .thenReturn(Optional.of(s));
        when(templateRepository.findById(2L))
                .thenReturn(Optional.of(t));
        when(certificateRepository.save(any(Certificate.class)))
                .thenAnswer(i -> {
                    Certificate c = i.getArgument(0);
                    c.setId(99L);
                    return c;
                });

        Certificate c = certificateService.generateCertificate(1L, 2L);
        Assert.assertTrue(c.getVerificationCode().startsWith("VC-"));
        Assert.assertTrue(c.getQrCodeUrl().startsWith("data:image/png;base64,"));
    }

    @Test
    public void t06_findByVerificationCode() {
        Certificate c = Certificate.builder()
                .id(77L)
                .verificationCode("VC-777")
                .build();

        when(certificateRepository.findByVerificationCode("VC-777"))
                .thenReturn(Optional.of(c));

        Certificate found = certificateService.findByVerificationCode("VC-777");
        Assert.assertEquals(found.getId().longValue(), 77L);
    }

    @Test
    public void t07_listCertificatesByStudent() {
        Student s = Student.builder().id(3L).build();
        Certificate c = Certificate.builder().id(300L).student(s).build();

        when(studentRepository.findById(3L))
                .thenReturn(Optional.of(s));
        when(certificateRepository.findByStudent(s))
                .thenReturn(List.of(c));

        List<Certificate> list = certificateService.findByStudentId(3L);
        Assert.assertEquals(list.size(), 1);
    }
}

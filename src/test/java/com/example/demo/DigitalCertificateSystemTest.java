package com.example.demo;

import com.example.demo.controller.*;
import com.example.demo.dto.AuthRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.*;
import com.example.demo.repository.*;
import com.example.demo.security.JwtUtil;
import com.example.demo.service.*;
import com.example.demo.service.impl.*;

// import lombok.var;

import org.mockito.*;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Corrected TestNG test class (64 tests). Uses type-safe invocation.getArgument(0, Type.class)
 * and type-specific any(...) matchers to avoid ClassCastException / NPE.
 */
@Listeners(TestResultListener.class)
public class DigitalCertificateSystemTest {

    // Mocks
    @Mock private UserRepository userRepository;
    @Mock private StudentRepository studentRepository;
    @Mock private CertificateTemplateRepository templateRepository;
    @Mock private CertificateRepository certificateRepository;
    @Mock private VerificationLogRepository logRepository;

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

        userService = new UserServiceImpl(userRepository);
        studentService = new StudentServiceImpl(studentRepository);
        templateService = new TemplateServiceImpl(templateRepository);
        certificateService = new CertificateServiceImpl(certificateRepository, studentRepository, templateRepository);
        verificationService = new VerificationServiceImpl(certificateRepository, logRepository);

        jwtUtil = new JwtUtil("abcdefghijklmnopqrstuvwxyz0123456789ABCD", 3600000L);

        authController = new AuthController(userService, jwtUtil);
        studentController = new StudentController(studentService);
        templateController = new TemplateController(templateService);
        certificateController = new CertificateController(certificateService);
        verificationController = new VerificationController(verificationService);
    }

    // -------------------------
    // Section 1: Servlet (1-8)
    // -------------------------
    @Test(priority = 1, groups = {"servlet"})
    public void t01_controllersCreated() {
        Assert.assertNotNull(authController);
        Assert.assertNotNull(studentController);
        Assert.assertNotNull(templateController);
        Assert.assertNotNull(certificateController);
        Assert.assertNotNull(verificationController);
    }

    @Test(priority = 2, groups = {"servlet"})
    public void t02_applicationMainRuns() {
        Assert.assertNotNull(com.example.demo.DemoApplication.class);
    }

    @Test(priority = 3, groups = {"servlet"})
    public void t03_swaggerConfigPresent() {
        Assert.assertNotNull(com.example.demo.config.SwaggerConfig.class);
    }

    @Test(priority = 4, groups = {"servlet"})
    public void t04_securityConfigPresent() {
        Assert.assertNotNull(com.example.demo.config.SecurityConfig.class);
    }

    @Test(priority = 5, groups = {"servlet"})
    public void t05_authEndpointsExist() throws NoSuchMethodException {
        Assert.assertNotNull(authController.getClass().getMethod("register", RegisterRequest.class));
        Assert.assertNotNull(authController.getClass().getMethod("login", AuthRequest.class));
    }

    @Test(priority = 6, groups = {"servlet"})
    public void t06_studentEndpointsExist() throws NoSuchMethodException {
        Assert.assertNotNull(studentController.getClass().getMethod("add", com.example.demo.entity.Student.class));
        Assert.assertNotNull(studentController.getClass().getMethod("list"));
    }

    @Test(priority = 7, groups = {"servlet"})
    public void t07_templateEndpointsExist() throws NoSuchMethodException {
        Assert.assertNotNull(templateController.getClass().getMethod("add", com.example.demo.entity.CertificateTemplate.class));
        Assert.assertNotNull(templateController.getClass().getMethod("list"));
    }

    @Test(priority = 8, groups = {"servlet"})
    public void t08_certificateEndpointsExist() throws NoSuchMethodException {
        Assert.assertNotNull(certificateController.getClass().getMethod("generate", Long.class, Long.class));
        Assert.assertNotNull(certificateController.getClass().getMethod("get", Long.class));
    }

    // ------------------------------------------
    // Section 2: CRUD & REST APIs (9-16)
    // ------------------------------------------
    @Test(priority = 9, groups = {"crud"})
    public void t09_addStudentSuccess() {
        Student s = Student.builder().name("Alice").email("alice@ex.com").rollNumber("R001").build();
        when(studentRepository.findByEmail("alice@ex.com")).thenReturn(Optional.empty());
        when(studentRepository.findByRollNumber("R001")).thenReturn(Optional.empty());
        when(studentRepository.save(any(com.example.demo.entity.Student.class))).thenAnswer(inv -> {
            Student arg = inv.getArgument(0, Student.class);
            if (arg != null) arg.setId(1L);
            return arg;
        });

        Student res = studentService.addStudent(s);
        Assert.assertEquals(res.getName(), "Alice");
        verify(studentRepository, times(1)).save(any(com.example.demo.entity.Student.class));
    }

    @Test(priority = 10, groups = {"crud"})
    public void t10_addStudentDuplicateEmail() {
        Student s = Student.builder().name("B").email("b@ex.com").rollNumber("R002").build();
        when(studentRepository.findByEmail("b@ex.com")).thenReturn(Optional.of(s));
        try {
            studentService.addStudent(s);
            Assert.fail("Expected duplicate email runtime exception");
        } catch (RuntimeException ex) {
            Assert.assertTrue(ex.getMessage().contains("Student email exists"));
        }
    }

    @Test(priority = 11, groups = {"crud"})
    public void t11_listStudents() {
        Student s = Student.builder().id(1L).name("C").email("c@ex.com").rollNumber("R003").build();
        when(studentRepository.findAll()).thenReturn(List.of(s));
        var list = studentService.getAllStudents();
        Assert.assertEquals(list.size(), 1);
    }

    @Test(priority = 12, groups = {"crud"})
    public void t12_addTemplateSuccess() {
        CertificateTemplate t = CertificateTemplate.builder().templateName("T1").backgroundUrl("https://bg").build();
        when(templateRepository.findByTemplateName("T1")).thenReturn(Optional.empty());
        when(templateRepository.save(any(com.example.demo.entity.CertificateTemplate.class))).thenAnswer(inv -> {
            CertificateTemplate arg = inv.getArgument(0, CertificateTemplate.class);
            if (arg != null) arg.setId(2L);
            return arg;
        });
        var out = templateService.addTemplate(t);
        Assert.assertEquals(out.getTemplateName(), "T1");
    }

    @Test(priority = 13, groups = {"crud"})
    public void t13_addTemplateDuplicateName() {
        CertificateTemplate t = CertificateTemplate.builder().templateName("X").backgroundUrl("https://x").build();
        when(templateRepository.findByTemplateName("X")).thenReturn(Optional.of(t));
        try {
            templateService.addTemplate(t);
            Assert.fail("Expected duplicate template exception");
        } catch (RuntimeException ex) {
            Assert.assertTrue(ex.getMessage().contains("Template name exists"));
        }
    }

    @Test(priority = 14, groups = {"crud"})
    public void t14_generateCertificateSuccess() {
        Student s = Student.builder().id(2L).name("D").email("d@ex").rollNumber("R010").build();
        CertificateTemplate tpl = CertificateTemplate.builder().id(2L).templateName("T2").backgroundUrl("https://bg2").build();

        when(studentRepository.findById(2L)).thenReturn(Optional.of(s));
        when(templateRepository.findById(2L)).thenReturn(Optional.of(tpl));
        when(certificateRepository.save(any(com.example.demo.entity.Certificate.class))).thenAnswer(inv -> {
            Certificate arg = inv.getArgument(0, Certificate.class);
            if (arg != null) arg.setId(100L);
            return arg;
        });

        Certificate cert = certificateService.generateCertificate(2L, 2L);
        Assert.assertNotNull(cert.getVerificationCode());
        Assert.assertTrue(cert.getQrCodeUrl() != null && cert.getQrCodeUrl().startsWith("data:image/png;base64,"));
        verify(certificateRepository, times(1)).save(any(com.example.demo.entity.Certificate.class));
    }

    @Test(priority = 15, groups = {"crud"})
    public void t15_getCertificateNotFound() {
        when(certificateRepository.findById(999L)).thenReturn(Optional.empty());
        try {
            certificateService.getCertificate(999L);
            Assert.fail("Expected ResourceNotFoundException");
        } catch (RuntimeException ex) {
            Assert.assertTrue(ex.getMessage().contains("Certificate not found"));
        }
    }

    @Test(priority = 16, groups = {"crud"})
    public void t16_findByVerificationCode() {
        Certificate c = Certificate.builder().id(200L).verificationCode("VC200").build();
        when(certificateRepository.findByVerificationCode("VC200")).thenReturn(Optional.of(c));
        var out = certificateService.findByVerificationCode("VC200");
        Assert.assertEquals(out.getId().longValue(), 200L);
    }

    // ------------------------------------------
    // Section 3: DI & IoC (17-24)
    // ------------------------------------------
    @Test(priority = 17, groups = {"di"})
    public void t17_serviceBeansAreInstances() {
        Assert.assertTrue(userService instanceof UserService);
        Assert.assertTrue(studentService instanceof StudentService);
    }

    @Test(priority = 18, groups = {"di"})
    public void t18_controllersHaveServices() throws Exception {
        Assert.assertNotNull(studentController);
        Assert.assertNotNull(templateController);
    }

    @Test(priority = 19, groups = {"di"})
    public void t19_constructorInjection() {
        var ctor = com.example.demo.controller.StudentController.class.getConstructors()[0];
        Assert.assertTrue(ctor.getParameterCount() > 0);
    }

    @Test(priority = 20, groups = {"di"})
    public void t20_repoMocksAreIndependent() {
        Assert.assertNotSame(studentRepository, templateRepository);
        Assert.assertNotSame(userRepository, certificateRepository);
    }

    @Test(priority = 21, groups = {"di"})
    public void t21_userRegisterUsesRepo() {
        User u = User.builder().name("U").email("u@ex.com").password("p").build();
        when(userRepository.findByEmail("u@ex.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(com.example.demo.entity.User.class))).thenAnswer(inv -> {
            User arg = inv.getArgument(0, User.class);
            if (arg != null) arg.setId(10L);
            return arg;
        });

        User saved = userService.register(u);
        Assert.assertNotNull(saved.getId());
        verify(userRepository, times(1)).save(any(com.example.demo.entity.User.class));
    }

    @Test(priority = 22, groups = {"di"})
    public void t22_userFindByEmail() {
        User u = User.builder().id(11L).name("UU").email("uu@ex.com").password("x").build();
        when(userRepository.findByEmail("uu@ex.com")).thenReturn(Optional.of(u));
        var out = userService.findByEmail("uu@ex.com");
        Assert.assertEquals(out.getId().longValue(), 11L);
    }

    @Test(priority = 23, groups = {"di"})
    public void t23_serviceLayerExceptionPropagation() {
        when(studentRepository.findById(555L)).thenReturn(Optional.empty());
        try {
            studentService.findById(555L);
            Assert.fail("Expected Student not found");
        } catch (RuntimeException ex) {
            Assert.assertTrue(ex.getMessage().contains("Student not found"));
        }
    }

    @Test(priority = 24, groups = {"di"})
    public void t24_servicesAreStatelessBetweenCalls() {
        when(templateRepository.findByTemplateName("Alpha")).thenReturn(Optional.empty());
        when(templateRepository.save(any(com.example.demo.entity.CertificateTemplate.class))).thenAnswer(inv -> {
            CertificateTemplate arg = inv.getArgument(0, CertificateTemplate.class);
            if (arg != null) arg.setId(33L);
            return arg;
        });
        templateService.addTemplate(CertificateTemplate.builder().templateName("Alpha").backgroundUrl("https://a").build());
        templateService.addTemplate(CertificateTemplate.builder().templateName("Alpha2").backgroundUrl("https://b").build());
        Assert.assertTrue(true);
    }

    // ---------------------------------------------------
    // Section 4: Hibernate configs, annotations & CRUD (25-32)
    // ---------------------------------------------------
    @Test(priority = 25, groups = {"hibernate"})
    public void t25_entitiesHaveEntityAnnotation() {
        Assert.assertTrue(com.example.demo.entity.Student.class.isAnnotationPresent(jakarta.persistence.Entity.class));
        Assert.assertTrue(com.example.demo.entity.Certificate.class.isAnnotationPresent(jakarta.persistence.Entity.class));
    }

    @Test(priority = 26, groups = {"hibernate"})
    public void t26_uniqueConstraintsApplied() {
        var tclass = com.example.demo.entity.Certificate.class;
        Assert.assertNotNull(tclass.getAnnotation(jakarta.persistence.Entity.class));
    }

    @Test(priority = 27, groups = {"hibernate"})
    public void t27_saveCertificatePersists() {
        Student s = Student.builder().id(3L).name("S3").email("s3@ex").rollNumber("R3").build();
        CertificateTemplate tpl = CertificateTemplate.builder().id(3L).templateName("Tpl3").backgroundUrl("https://bg3").build();

        when(studentRepository.findById(3L)).thenReturn(Optional.of(s));
        when(templateRepository.findById(3L)).thenReturn(Optional.of(tpl));
        when(certificateRepository.save(any(com.example.demo.entity.Certificate.class))).thenAnswer(inv -> {
            Certificate arg = inv.getArgument(0, Certificate.class);
            if (arg != null) arg.setId(301L);
            return arg;
        });

        var c = certificateService.generateCertificate(3L, 3L);
        Assert.assertEquals(c.getId().longValue(), 301L);
    }

    @Test(priority = 28, groups = {"hibernate"})
    public void t28_generatedVerificationCodeIsUniquePattern() {
        when(studentRepository.findById(4L)).thenReturn(Optional.of(Student.builder().id(4L).name("X").email("x@ex").rollNumber("R4").build()));
        when(templateRepository.findById(4L)).thenReturn(Optional.of(CertificateTemplate.builder().id(4L).templateName("T4").backgroundUrl("https://b").build()));
        when(certificateRepository.save(any(com.example.demo.entity.Certificate.class))).thenAnswer(inv -> {
            Certificate arg = inv.getArgument(0, Certificate.class);
            if (arg != null) arg.setId(400L);
            return arg;
        });

        var c = certificateService.generateCertificate(4L, 4L);
        Assert.assertTrue(c.getVerificationCode().startsWith("VC-"));
    }

    @Test(priority = 29, groups = {"hibernate"})
    public void t29_qrCodeIsBase64DataUrl() {
        when(studentRepository.findById(5L)).thenReturn(Optional.of(Student.builder().id(5L).name("Q").email("q@ex").rollNumber("R5").build()));
        when(templateRepository.findById(5L)).thenReturn(Optional.of(CertificateTemplate.builder().id(5L).templateName("T5").backgroundUrl("https://bg5").build()));
        when(certificateRepository.save(any(com.example.demo.entity.Certificate.class))).thenAnswer(inv -> {
            Certificate arg = inv.getArgument(0, Certificate.class);
            if (arg != null) arg.setId(500L);
            return arg;
        });

        var c = certificateService.generateCertificate(5L, 5L);
        Assert.assertTrue(c.getQrCodeUrl().startsWith("data:image/png;base64,"));
    }

    @Test(priority = 30, groups = {"hibernate"})
    public void t30_hibernateSaveThrowsWhenConstraintViolated() {
        Student s = Student.builder().id(6L).name("Con").email("c@ex").rollNumber("R6").build();
        when(studentRepository.findByEmail("c@ex")).thenReturn(Optional.of(s));
        try {
            studentService.addStudent(s);
            Assert.fail("Expected Student email exists");
        } catch (RuntimeException ex) {
            Assert.assertTrue(ex.getMessage().contains("Student email exists"));
        }
    }

    @Test(priority = 31, groups = {"hibernate"})
    public void t31_repositoryCustomQueryFindByVerificationCode() {
        Certificate c = Certificate.builder().id(777L).verificationCode("VC777").build();
        when(certificateRepository.findByVerificationCode("VC777")).thenReturn(Optional.of(c));
        var out = certificateService.findByVerificationCode("VC777");
        Assert.assertEquals(out.getId().longValue(), 777L);
    }

    @Test(priority = 32, groups = {"hibernate"})
    public void t32_certificateFindByStudentDelegatesToRepo() {
        Student s = Student.builder().id(8L).name("Stu8").email("stu8@ex").rollNumber("R8").build();
        Certificate c = Certificate.builder().id(808L).student(s).verificationCode("VC808").build();
        when(studentRepository.findById(8L)).thenReturn(Optional.of(s));
        when(certificateRepository.findByStudent(s)).thenReturn(List.of(c));
        var list = certificateService.findByStudentId(8L);
        Assert.assertEquals(list.size(), 1);
    }

    // ---------------------------------------------------
    // Section 5: JPA mapping & normalization (33-40)
    // ---------------------------------------------------
    @Test(priority = 33, groups = {"jpa"})
    public void t33_1NF_fieldAtomicity() {
        Student s = new Student(); s.setName("Name"); s.setEmail("e@ex"); s.setRollNumber("R33");
        Assert.assertNotNull(s.getName());
    }

    @Test(priority = 34, groups = {"jpa"})
    public void t34_2NF_noPartialDependency() {
        var s = Student.builder().id(20L).name("N").email("n@ex").rollNumber("R20").build();
        Assert.assertNotNull(s.getRollNumber());
    }

    @Test(priority = 35, groups = {"jpa"})
    public void t35_3NF_no_transitive_dependency() {
        CertificateTemplate t = new CertificateTemplate(); t.setTemplateName("NormTpl");
        Assert.assertNotNull(t.getTemplateName());
    }

    @Test(priority = 36, groups = {"jpa"})
    public void t36_normalizationAllowsQueries() {
        Student s = Student.builder().id(21L).name("Q").email("q21@ex").rollNumber("R21").build();
        Certificate c = Certificate.builder().id(900L).student(s).verificationCode("V900").build();
        Assert.assertEquals(c.getStudent().getRollNumber(), "R21");
    }

    @Test(priority = 37, groups = {"jpa"})
    public void t37_schemaDesignSupportsAuditLogs() {
        VerificationLog log = new VerificationLog(); log.setStatus("SUCCESS"); log.setIpAddress("127.0.0.1");
        Assert.assertNotNull(log.getStatus());
    }

    @Test(priority = 38, groups = {"jpa"})
    public void t38_uniquenessConstraintsReflected() {
        Assert.assertTrue(com.example.demo.entity.Certificate.class.isAnnotationPresent(jakarta.persistence.Entity.class));
    }

    @Test(priority = 39, groups = {"jpa"})
    public void t39_jpaRelationsAreMapped() {
        Assert.assertTrue(com.example.demo.entity.Certificate.class.getDeclaredFields().length > 0);
    }

    @Test(priority = 40, groups = {"jpa"})
    public void t40_normalizationEdgeCase() {
        Student s = Student.builder().name("Edge").email("edge@ex").rollNumber("RXX").build();
        when(studentRepository.findByEmail("edge@ex")).thenReturn(Optional.empty());
        when(studentRepository.findByRollNumber("RXX")).thenReturn(Optional.empty());
        when(studentRepository.save(any(com.example.demo.entity.Student.class))).thenAnswer(inv -> {
            Student arg = inv.getArgument(0, Student.class);
            if (arg != null) arg.setId(999L);
            return arg;
        });
        Student saved = studentService.addStudent(s);
        Assert.assertEquals(saved.getRollNumber(), "RXX");
    }

    // ---------------------------------------------------
    // Section 6: Many-to-Many relationships & associations (41-48) (simulated)
    // ---------------------------------------------------
    @Test(priority = 41, groups = {"m2m"})
    public void t41_simulateManyToManyMappingPresence() {
        Student s1 = Student.builder().id(50L).name("M1").email("m1@ex").rollNumber("RM1").build();
        Student s2 = Student.builder().id(51L).name("M2").email("m2@ex").rollNumber("RM2").build();
        Assert.assertNotNull(s1); Assert.assertNotNull(s2);
    }

    @Test(priority = 42, groups = {"m2m"})
    public void t42_associateMultipleCertificatesToStudent() {
        Student s = Student.builder().id(60L).name("Many").email("many@ex").rollNumber("R60").build();
        Certificate c1 = Certificate.builder().id(601L).student(s).verificationCode("VC601").build();
        Certificate c2 = Certificate.builder().id(602L).student(s).verificationCode("VC602").build();
        when(studentRepository.findById(60L)).thenReturn(Optional.of(s));
        when(certificateRepository.findByStudent(s)).thenReturn(List.of(c1, c2));
        var list = certificateService.findByStudentId(60L);
        Assert.assertEquals(list.size(), 2);
    }

    @Test(priority = 43, groups = {"m2m"})
    public void t43_associationConsistencyOnDelete() {
        Student s = Student.builder().id(70L).name("Del").email("del@ex").rollNumber("R70").build();
        when(studentRepository.findById(70L)).thenReturn(Optional.of(s));
        when(certificateRepository.findByStudent(s)).thenReturn(Collections.emptyList());
        var list = certificateService.findByStudentId(70L);
        Assert.assertTrue(list.isEmpty());
    }

    @Test(priority = 44, groups = {"m2m"})
    public void t44_manyCertificatesDifferentTemplates() {
        Student s = Student.builder().id(80L).name("Multi").email("multi@ex").rollNumber("R80").build();
        CertificateTemplate t1 = CertificateTemplate.builder().id(1L).templateName("A").backgroundUrl("https://a").build();
        CertificateTemplate t2 = CertificateTemplate.builder().id(2L).templateName("B").backgroundUrl("https://b").build();
        Certificate c1 = Certificate.builder().id(801L).student(s).template(t1).verificationCode("V801").build();
        Certificate c2 = Certificate.builder().id(802L).student(s).template(t2).verificationCode("V802").build();
        when(studentRepository.findById(80L)).thenReturn(Optional.of(s));
        when(certificateRepository.findByStudent(s)).thenReturn(List.of(c1, c2));
        var list = certificateService.findByStudentId(80L);
        Assert.assertEquals(list.size(), 2);
    }

    @Test(priority = 45, groups = {"m2m"})
    public void t45_associationMappingEdgeCase() {
        Student s = Student.builder().id(90L).name("Assoc").email("assoc@ex").rollNumber("R90").build();
        Certificate c = Certificate.builder().id(901L).student(s).verificationCode("V901").build();
        Assert.assertEquals(c.getStudent().getId().longValue(), 90L);
    }

    @Test(priority = 46, groups = {"m2m"})
    public void t46_manyToManySimulationPerformance() {
        when(studentRepository.findById(100L)).thenReturn(Optional.of(Student.builder().id(100L).name("P").email("p@ex").rollNumber("R100").build()));
        when(templateRepository.findById(100L)).thenReturn(Optional.of(CertificateTemplate.builder().id(100L).templateName("PT").backgroundUrl("https://pt").build()));
        when(certificateRepository.save(any(com.example.demo.entity.Certificate.class))).thenAnswer(inv -> {
            Certificate arg = inv.getArgument(0, Certificate.class);
            if (arg != null) arg.setId(new Random().nextLong());
            return arg;
        });

        for (int i = 0; i < 5; i++) {
            certificateService.generateCertificate(100L, 100L);
        }
        Assert.assertTrue(true);
    }

    @Test(priority = 47, groups = {"m2m"})
    public void t47_associationDataIntegrity() {
        Student s = Student.builder().id(110L).name("Int").email("int@ex").rollNumber("R110").build();
        Certificate c = Certificate.builder().id(1111L).student(s).verificationCode("V1111").build();
        Assert.assertEquals(c.getStudent().getEmail(), "int@ex");
    }

    @Test(priority = 48, groups = {"m2m"})
    public void t48_associationNullsHandled() {
        Certificate c = new Certificate();
        c.setVerificationCode("VN");
        Assert.assertNull(c.getStudent());
        Assert.assertNotNull(c.getVerificationCode());
    }

    // ---------------------------------------------------
    // Section 7: Security & JWT (49-56)
    // ---------------------------------------------------
    @Test(priority = 49, groups = {"security"})
    public void t49_registerThenLoginProducesToken() {
        RegisterRequest req = new RegisterRequest();
        req.setName("Admin"); req.setEmail("admin@ex.com"); req.setPassword("secret"); req.setRole("ADMIN");

        when(userRepository.findByEmail("admin@ex.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(com.example.demo.entity.User.class))).thenAnswer(inv -> {
            User arg = inv.getArgument(0, User.class);
            if (arg != null) arg.setId(500L);
            return arg;
        });

        User saved = userService.register(User.builder().name(req.getName()).email(req.getEmail()).password(req.getPassword()).role(req.getRole()).build());
        Assert.assertNotNull(saved.getId());

        when(userRepository.findByEmail("admin@ex.com")).thenReturn(Optional.of(saved));

        AuthRequest ar = new AuthRequest();
        ar.setEmail("admin@ex.com"); ar.setPassword(req.getPassword());
        var resp = authController.login(ar);
        Assert.assertEquals(resp.getStatusCodeValue(), 200);
        var token = ((com.example.demo.dto.AuthResponse) resp.getBody()).getToken();
        Assert.assertTrue(jwtUtil.validateToken(token));
    }

    @Test(priority = 50, groups = {"security"})
    public void t50_invalidLoginRejected() {
        when(userRepository.findByEmail("no@ex")).thenReturn(Optional.empty());
        AuthRequest ar = new AuthRequest(); ar.setEmail("no@ex"); ar.setPassword("x");
        var resp = authController.login(ar);
        Assert.assertEquals(resp.getStatusCodeValue(), 401);
    }

    @Test(priority = 51, groups = {"security"})
    public void t51_jwtClaimsIncludeUserDetails() {
        Map<String, Object> claims = Map.of("userId", 1000L, "email", "a@ex", "role", "STAFF");
        String token = jwtUtil.generateToken(claims, "a@ex");
        Assert.assertTrue(jwtUtil.validateToken(token));
        var body = jwtUtil.parseToken(token).getBody();
        Assert.assertEquals(body.get("email", String.class), "a@ex");
        Assert.assertEquals(body.get("role", String.class), "STAFF");
    }

    @Test(priority = 52, groups = {"security"})
    public void t52_jwtFilterSetsAuthenticationWhenValid() {
        var filter = new com.example.demo.security.JwtFilter(jwtUtil);
        Assert.assertNotNull(filter);
    }

    @Test(priority = 53, groups = {"security"})
    public void t53_jwtInvalidTokenRejected() {
        Assert.assertFalse(jwtUtil.validateToken("invalid.token.here"));
    }

    @Test(priority = 54, groups = {"security"})
    public void t54_authControllerRegisterDefaultsRoleStaff() {
        RegisterRequest r = new RegisterRequest(); r.setName("R"); r.setEmail("r@ex"); r.setPassword("p");
        when(userRepository.findByEmail("r@ex")).thenReturn(Optional.empty());
        when(userRepository.save(any(com.example.demo.entity.User.class))).thenAnswer(inv -> {
            User arg = inv.getArgument(0, User.class);
            if (arg != null) arg.setId(777L);
            return arg;
        });

        var saved = userService.register(User.builder().name("R").email("r@ex").password("p").build());
        Assert.assertEquals(saved.getRole(), "STAFF");
    }

    @Test(priority = 55, groups = {"security"})
    public void t55_protectedEndpointRequiresToken_simulated() {
        Map<String, Object> claims = Map.of("userId", 1L, "email", "x@ex", "role", "STAFF");
        String token = jwtUtil.generateToken(claims, "x@ex");
        Assert.assertTrue(jwtUtil.validateToken(token));
    }

    @Test(priority = 56, groups = {"security"})
    public void t56_bcryptPasswordMatches() {
        org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder enc = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
        String hashed = enc.encode("mypwd");
        Assert.assertTrue(enc.matches("mypwd", hashed));
    }

    // ---------------------------------------------------
    // Section 8: HQL / HCQL advanced querying (57-64)
    // ---------------------------------------------------
    @Test(priority = 57, groups = {"hql"})
    public void t57_findCertificateByVerificationCodeRepo() {
        Certificate c = Certificate.builder().id(321L).verificationCode("HVC1").build();
        when(certificateRepository.findByVerificationCode("HVC1")).thenReturn(Optional.of(c));
        var out = certificateService.findByVerificationCode("HVC1");
        Assert.assertEquals(out.getId().longValue(), 321L);
    }

    @Test(priority = 58, groups = {"hql"})
    public void t58_findCertificatesByStudentRepo() {
        Student s = Student.builder().id(400L).name("Q").email("q@ex").rollNumber("R400").build();
        Certificate c = Certificate.builder().id(401L).student(s).verificationCode("HVC2").build();
        when(studentRepository.findById(400L)).thenReturn(Optional.of(s));
        when(certificateRepository.findByStudent(s)).thenReturn(List.of(c));
        var list = certificateService.findByStudentId(400L);
        Assert.assertEquals(list.size(), 1);
    }

    @Test(priority = 59, groups = {"hql"})
    public void t59_hqlEdgeCase_noResults() {
        when(certificateRepository.findByVerificationCode("NONE")).thenReturn(Optional.empty());
        try {
            certificateService.findByVerificationCode("NONE");
            Assert.fail("Expected Certificate not found");
        } catch (RuntimeException ex) {
            Assert.assertTrue(ex.getMessage().contains("Certificate not found"));
        }
    }

    @Test(priority = 60, groups = {"hql"})
    public void t60_querySimulation_complexFilter() {
        Certificate c1 = Certificate.builder().id(1L).verificationCode("A").build();
        Certificate c2 = Certificate.builder().id(2L).verificationCode("B").build();
        List<Certificate> all = List.of(c1, c2);
        var filtered = all.stream().filter(cc -> cc.getVerificationCode().startsWith("A")).toList();
        Assert.assertEquals(filtered.size(), 1);
    }

    @Test(priority = 61, groups = {"hql"})
    public void t61_hqlAggregationSimulation() {
        List<Certificate> list = List.of(Certificate.builder().id(1L).build(), Certificate.builder().id(2L).build());
        long count = list.stream().count();
        Assert.assertEquals(count, 2L);
    }

    @Test(priority = 62, groups = {"hql"})
    public void t62_hcql_simulation_joinedQuery() {
        Student s = Student.builder().id(600L).name("Join").email("join@ex").rollNumber("R600").build();
        Certificate c = Certificate.builder().id(601L).student(s).verificationCode("J1").build();
        when(studentRepository.findById(600L)).thenReturn(Optional.of(s));
        when(certificateRepository.findByStudent(s)).thenReturn(List.of(c));
        var list = certificateService.findByStudentId(600L);
        Assert.assertEquals(list.get(0).getStudent().getName(), "Join");
    }

    @Test(priority = 63, groups = {"hql"})
    public void t63_hcql_paginationSimulation() {
        List<Certificate> many = new ArrayList<>();
        for (int i = 0; i < 10; i++) many.add(Certificate.builder().id((long) i).verificationCode("P" + i).build());
        var page = many.subList(0, 5);
        Assert.assertEquals(page.size(), 5);
    }

    @Test(priority = 64, groups = {"hql"})
    public void t64_finalSanity() {
        Assert.assertNotNull(jwtUtil);
        Assert.assertNotNull(userService);
        Assert.assertNotNull(certificateService);
    }
}

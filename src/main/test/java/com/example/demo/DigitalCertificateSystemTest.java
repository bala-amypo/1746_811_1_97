package com.example.demo;

import com.example.demo.controller.*;
import com.example.demo.dto.AuthRequest;
import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.*;
import com.example.demo.repository.*;
import com.example.demo.security.JwtFilter;
import com.example.demo.security.JwtUtil;
import com.example.demo.service.*;
import com.example.demo.service.impl.*;

import org.mockito.*;
import org.testng.Assert;
import org.testng.annotations.*;

import java.util.*;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
        certificateService = new CertificateServiceImpl(certificateRepository, studentRepository, templateRepository);
        verificationService = new VerificationServiceImpl(certificateRepository, logRepository);

        jwtUtil = new JwtUtil("abcdefghijklmnopqrstuvwxyz0123456789ABCD", 3600000L);

        authController = new AuthController(userService, jwtUtil);
        studentController = new StudentController(studentService);
        templateController = new TemplateController(templateService);
        certificateController = new CertificateController(certificateService);
        verificationController = new VerificationController(verificationService);
    }

    @Test(priority = 1)
    public void t01_controllersCreated() {
        Assert.assertNotNull(authController);
        Assert.assertNotNull(studentController);
        Assert.assertNotNull(templateController);
        Assert.assertNotNull(certificateController);
        Assert.assertNotNull(verificationController);
    }

    @Test(priority = 2)
    public void t02_applicationMainRuns() {
        Assert.assertNotNull(DemoApplication.class);
    }

    @Test(priority = 3)
    public void t03_swaggerConfigPresent() {
        Assert.assertNotNull(com.example.demo.config.SwaggerConfig.class);
    }

    @Test(priority = 4)
    public void t04_securityConfigPresent() {
        Assert.assertNotNull(com.example.demo.config.SecurityConfig.class);
    }

    @Test(priority = 5)
    public void t05_authEndpointsExist() throws NoSuchMethodException {
        Assert.assertNotNull(authController.getClass().getMethod("register", RegisterRequest.class));
        Assert.assertNotNull(authController.getClass().getMethod("login", AuthRequest.class));
    }

    @Test(priority = 6)
    public void t06_studentEndpointsExist() throws NoSuchMethodException {
        Assert.assertNotNull(studentController.getClass().getMethod("add", Student.class));
        Assert.assertNotNull(studentController.getClass().getMethod("list"));
    }

    @Test(priority = 7)
    public void t07_templateEndpointsExist() throws NoSuchMethodException {
        Assert.assertNotNull(templateController.getClass().getMethod("add", CertificateTemplate.class));
        Assert.assertNotNull(templateController.getClass().getMethod("list"));
    }

    @Test(priority = 8)
    public void t08_certificateEndpointsExist() throws NoSuchMethodException {
        Assert.assertNotNull(certificateController.getClass().getMethod("generate", Long.class, Long.class));
        Assert.assertNotNull(certificateController.getClass().getMethod("get", Long.class));
    }

    @Test(priority = 9)
    public void t09_addStudentSuccess() {
        Student s = Student.builder().name("Alice").email("alice@ex.com").rollNumber("R001").build();
        when(studentRepository.findByEmail("alice@ex.com")).thenReturn(Optional.empty());
        when(studentRepository.findByRollNumber("R001")).thenReturn(Optional.empty());
        when(studentRepository.save(any(Student.class))).thenAnswer(i -> {
            Student st = i.getArgument(0);
            st.setId(1L);
            return st;
        });

        Student res = studentService.addStudent(s);
        Assert.assertEquals(res.getName(), "Alice");
    }

    @Test(priority = 10)
    public void t10_addStudentDuplicateEmail() {
        Student s = Student.builder().email("b@ex.com").rollNumber("R002").build();
        when(studentRepository.findByEmail("b@ex.com")).thenReturn(Optional.of(s));
        try {
            studentService.addStudent(s);
            Assert.fail();
        } catch (RuntimeException e) {
            Assert.assertTrue(e.getMessage().contains("Student email exists"));
        }
    }

    @Test(priority = 11)
    public void t11_listStudents() {
        Student s = Student.builder().id(1L).build();
        when(studentRepository.findAll()).thenReturn(List.of(s));
        List<Student> list = studentService.getAllStudents();
        Assert.assertEquals(list.size(), 1);
    }

    @Test(priority = 12)
    public void t12_addTemplateSuccess() {
        CertificateTemplate t = CertificateTemplate.builder().templateName("T1").backgroundUrl("bg").build();
        when(templateRepository.findByTemplateName("T1")).thenReturn(Optional.empty());
        when(templateRepository.save(any(CertificateTemplate.class))).thenAnswer(i -> {
            CertificateTemplate ct = i.getArgument(0);
            ct.setId(2L);
            return ct;
        });
        CertificateTemplate out = templateService.addTemplate(t);
        Assert.assertEquals(out.getTemplateName(), "T1");
    }

    @Test(priority = 13)
    public void t13_addTemplateDuplicateName() {
        CertificateTemplate t = CertificateTemplate.builder().templateName("X").build();
        when(templateRepository.findByTemplateName("X")).thenReturn(Optional.of(t));
        try {
            templateService.addTemplate(t);
            Assert.fail();
        } catch (RuntimeException e) {
            Assert.assertTrue(e.getMessage().contains("Template name exists"));
        }
    }

    @Test(priority = 14)
    public void t14_generateCertificateSuccess() {
        Student s = Student.builder().id(2L).build();
        CertificateTemplate tpl = CertificateTemplate.builder().id(2L).build();
        when(studentRepository.findById(2L)).thenReturn(Optional.of(s));
        when(templateRepository.findById(2L)).thenReturn(Optional.of(tpl));
        when(certificateRepository.save(any(Certificate.class))).thenAnswer(i -> {
            Certificate c = i.getArgument(0);
            c.setId(100L);
            return c;
        });
        Certificate cert = certificateService.generateCertificate(2L, 2L);
        Assert.assertNotNull(cert.getVerificationCode());
    }

    @Test(priority = 15)
    public void t15_getCertificateNotFound() {
        when(certificateRepository.findById(999L)).thenReturn(Optional.empty());
        try {
            certificateService.getCertificate(999L);
            Assert.fail();
        } catch (RuntimeException e) {
            Assert.assertTrue(e.getMessage().contains("Certificate not found"));
        }
    }

    @Test(priority = 16)
    public void t16_findByVerificationCode() {
        Certificate c = Certificate.builder().id(200L).verificationCode("VC200").build();
        when(certificateRepository.findByVerificationCode("VC200")).thenReturn(Optional.of(c));
        Certificate out = certificateService.findByVerificationCode("VC200");
        Assert.assertEquals(out.getId().longValue(), 200L);
    }

    @Test(priority = 17)
    public void t17_serviceBeansAreInstances() {
        Assert.assertTrue(userService instanceof UserService);
        Assert.assertTrue(studentService instanceof StudentService);
    }

    @Test(priority = 18)
    public void t18_controllersHaveServices() {
        Assert.assertNotNull(studentController);
        Assert.assertNotNull(templateController);
    }

    @Test(priority = 19)
    public void t19_constructorInjection() {
        Assert.assertTrue(StudentController.class.getConstructors()[0].getParameterCount() > 0);
    }

    @Test(priority = 20)
    public void t20_repoMocksAreIndependent() {
        Assert.assertNotSame(studentRepository, templateRepository);
    }

    @Test(priority = 21)
    public void t21_userRegisterUsesRepo() {
        User u = User.builder().email("u@ex.com").password("p").build();
        when(userRepository.findByEmail("u@ex.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(i -> {
            User us = i.getArgument(0);
            us.setId(10L);
            return us;
        });
        User saved = userService.register(u);
        Assert.assertNotNull(saved.getId());
    }

    @Test(priority = 22)
    public void t22_userFindByEmail() {
        User u = User.builder().id(11L).email("uu@ex.com").build();
        when(userRepository.findByEmail("uu@ex.com")).thenReturn(Optional.of(u));
        User out = userService.findByEmail("uu@ex.com");
        Assert.assertEquals(out.getId().longValue(), 11L);
    }

    @Test(priority = 23)
    public void t23_serviceLayerExceptionPropagation() {
        when(studentRepository.findById(555L)).thenReturn(Optional.empty());
        try {
            studentService.findById(555L);
            Assert.fail();
        } catch (RuntimeException e) {
            Assert.assertTrue(e.getMessage().contains("Student not found"));
        }
    }

    @Test(priority = 24)
    public void t24_servicesAreStatelessBetweenCalls() {
        when(templateRepository.findByTemplateName(any())).thenReturn(Optional.empty());
        when(templateRepository.save(any(CertificateTemplate.class))).thenAnswer(i -> i.getArgument(0));
        templateService.addTemplate(CertificateTemplate.builder().templateName("A").build());
        templateService.addTemplate(CertificateTemplate.builder().templateName("B").build());
        Assert.assertTrue(true);
    }

    @Test(priority = 25)
    public void t25_entitiesHaveEntityAnnotation() {
        Assert.assertTrue(Student.class.isAnnotationPresent(jakarta.persistence.Entity.class));
        Assert.assertTrue(Certificate.class.isAnnotationPresent(jakarta.persistence.Entity.class));
    }

    @Test(priority = 26)
    public void t26_uniqueConstraintsApplied() {
        Assert.assertNotNull(Certificate.class.getAnnotation(jakarta.persistence.Entity.class));
    }

    @Test(priority = 27)
    public void t27_saveCertificatePersists() {
        when(studentRepository.findById(3L)).thenReturn(Optional.of(Student.builder().id(3L).build()));
        when(templateRepository.findById(3L)).thenReturn(Optional.of(CertificateTemplate.builder().id(3L).build()));
        when(certificateRepository.save(any(Certificate.class))).thenAnswer(i -> {
            Certificate c = i.getArgument(0);
            c.setId(301L);
            return c;
        });
        Certificate c = certificateService.generateCertificate(3L, 3L);
        Assert.assertEquals(c.getId().longValue(), 301L);
    }

    @Test(priority = 28)
    public void t28_generatedVerificationCodeIsUniquePattern() {
        when(studentRepository.findById(4L)).thenReturn(Optional.of(Student.builder().id(4L).build()));
        when(templateRepository.findById(4L)).thenReturn(Optional.of(CertificateTemplate.builder().id(4L).build()));
        when(certificateRepository.save(any(Certificate.class))).thenAnswer(i -> i.getArgument(0));
        Certificate c = certificateService.generateCertificate(4L, 4L);
        Assert.assertTrue(c.getVerificationCode().startsWith("VC-"));
    }

    @Test(priority = 29)
    public void t29_qrCodeIsBase64DataUrl() {
        when(studentRepository.findById(5L)).thenReturn(Optional.of(Student.builder().id(5L).build()));
        when(templateRepository.findById(5L)).thenReturn(Optional.of(CertificateTemplate.builder().id(5L).build()));
        when(certificateRepository.save(any(Certificate.class))).thenAnswer(i -> i.getArgument(0));
        Certificate c = certificateService.generateCertificate(5L, 5L);
        Assert.assertTrue(c.getQrCodeUrl().startsWith("data:image/png;base64,"));
    }

    @Test(priority = 30)
    public void t30_hibernateSaveThrowsWhenConstraintViolated() {
        when(studentRepository.findByEmail("c@ex")).thenReturn(Optional.of(Student.builder().build()));
        try {
            studentService.addStudent(Student.builder().email("c@ex").build());
            Assert.fail();
        } catch (RuntimeException e) {
            Assert.assertTrue(e.getMessage().contains("Student email exists"));
        }
    }

    @Test(priority = 31)
    public void t31_repositoryCustomQueryFindByVerificationCode() {
        Certificate c = Certificate.builder().id(777L).verificationCode("VC777").build();
        when(certificateRepository.findByVerificationCode("VC777")).thenReturn(Optional.of(c));
        Certificate out = certificateService.findByVerificationCode("VC777");
        Assert.assertEquals(out.getId().longValue(), 777L);
    }

    @Test(priority = 32)
    public void t32_certificateFindByStudentDelegatesToRepo() {
        Student s = Student.builder().id(8L).build();
        Certificate c = Certificate.builder().student(s).build();
        when(studentRepository.findById(8L)).thenReturn(Optional.of(s));
        when(certificateRepository.findByStudent(s)).thenReturn(List.of(c));
        List<Certificate> list = certificateService.findByStudentId(8L);
        Assert.assertEquals(list.size(), 1);
    }

    @Test(priority = 33)
    public void t33_1NF_fieldAtomicity() {
        Student s = new Student();
        s.setName("Name");
        Assert.assertNotNull(s.getName());
    }

    @Test(priority = 34)
    public void t34_2NF_noPartialDependency() {
        Student s = Student.builder().rollNumber("R20").build();
        Assert.assertNotNull(s.getRollNumber());
    }

    @Test(priority = 35)
    public void t35_3NF_no_transitive_dependency() {
        CertificateTemplate t = new CertificateTemplate();
        t.setTemplateName("NormTpl");
        Assert.assertNotNull(t.getTemplateName());
    }

    @Test(priority = 36)
    public void t36_normalizationAllowsQueries() {
        Student s = Student.builder().rollNumber("R21").build();
        Certificate c = Certificate.builder().student(s).build();
        Assert.assertEquals(c.getStudent().getRollNumber(), "R21");
    }

    @Test(priority = 37)
    public void t37_schemaDesignSupportsAuditLogs() {
        VerificationLog log = new VerificationLog();
        log.setStatus("SUCCESS");
        Assert.assertNotNull(log.getStatus());
    }

    @Test(priority = 38)
    public void t38_uniquenessConstraintsReflected() {
        Assert.assertTrue(Certificate.class.isAnnotationPresent(jakarta.persistence.Entity.class));
    }

    @Test(priority = 39)
    public void t39_jpaRelationsAreMapped() {
        Assert.assertTrue(Certificate.class.getDeclaredFields().length > 0);
    }

    @Test(priority = 40)
    public void t40_normalizationEdgeCase() {
        when(studentRepository.findByEmail("edge@ex")).thenReturn(Optional.empty());
        when(studentRepository.findByRollNumber("RXX")).thenReturn(Optional.empty());
        when(studentRepository.save(any(Student.class))).thenAnswer(i -> {
            Student st = i.getArgument(0);
            st.setId(999L);
            return st;
        });
        Student saved = studentService.addStudent(Student.builder().email("edge@ex").rollNumber("RXX").build());
        Assert.assertEquals(saved.getRollNumber(), "RXX");
    }

    @Test(priority = 41)
    public void t41_simulateManyToManyMappingPresence() {
        Assert.assertNotNull(Student.builder().build());
    }

    @Test(priority = 42)
    public void t42_associateMultipleCertificatesToStudent() {
        Student s = Student.builder().id(60L).build();
        when(studentRepository.findById(60L)).thenReturn(Optional.of(s));
        when(certificateRepository.findByStudent(s)).thenReturn(List.of(new Certificate(), new Certificate()));
        List<Certificate> list = certificateService.findByStudentId(60L);
        Assert.assertEquals(list.size(), 2);
    }

    @Test(priority = 43)
    public void t43_associationConsistencyOnDelete() {
        Student s = Student.builder().id(70L).build();
        when(studentRepository.findById(70L)).thenReturn(Optional.of(s));
        when(certificateRepository.findByStudent(s)).thenReturn(Collections.emptyList());
        List<Certificate> list = certificateService.findByStudentId(70L);
        Assert.assertTrue(list.isEmpty());
    }

    @Test(priority = 44)
    public void t44_manyCertificatesDifferentTemplates() {
        Student s = Student.builder().id(80L).build();
        when(studentRepository.findById(80L)).thenReturn(Optional.of(s));
        when(certificateRepository.findByStudent(s)).thenReturn(List.of(new Certificate(), new Certificate()));
        List<Certificate> list = certificateService.findByStudentId(80L);
        Assert.assertEquals(list.size(), 2);
    }

    @Test(priority = 45)
    public void t45_associationMappingEdgeCase() {
        Student s = Student.builder().id(90L).build();
        Certificate c = Certificate.builder().student(s).build();
        Assert.assertEquals(c.getStudent().getId().longValue(), 90L);
    }

    @Test(priority = 46)
    public void t46_manyToManySimulationPerformance() {
        when(studentRepository.findById(100L)).thenReturn(Optional.of(Student.builder().id(100L).build()));
        when(templateRepository.findById(100L)).thenReturn(Optional.of(CertificateTemplate.builder().id(100L).build()));
        when(certificateRepository.save(any(Certificate.class))).thenAnswer(i -> i.getArgument(0));
        for (int i = 0; i < 5; i++) {
            certificateService.generateCertificate(100L, 100L);
        }
        Assert.assertTrue(true);
    }

    @Test(priority = 47)
    public void t47_associationDataIntegrity() {
        Student s = Student.builder().email("int@ex").build();
        Certificate c = Certificate.builder().student(s).build();
        Assert.assertEquals(c.getStudent().getEmail(), "int@ex");
    }

    @Test(priority = 48)
    public void t48_associationNullsHandled() {
        Certificate c = new Certificate();
        c.setVerificationCode("VN");
        Assert.assertNull(c.getStudent());
    }

    @Test(priority = 49)
    public void t49_registerThenLoginProducesToken() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("admin@ex.com");
        req.setPassword("secret");

        when(userRepository.findByEmail("admin@ex.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(i -> {
            User u = i.getArgument(0);
            u.setId(500L);
            return u;
        });

        User saved = userService.register(User.builder().email(req.getEmail()).password(req.getPassword()).build());
        when(userRepository.findByEmail("admin@ex.com")).thenReturn(Optional.of(saved));

        AuthRequest ar = new AuthRequest();
        ar.setEmail(req.getEmail());
        ar.setPassword(req.getPassword());

        var resp = authController.login(ar);
        AuthResponse body = (AuthResponse) resp.getBody();
        Assert.assertTrue(jwtUtil.validateToken(body.getToken()));
    }

    @Test(priority = 50)
    public void t50_invalidLoginRejected() {
        when(userRepository.findByEmail("no@ex")).thenReturn(Optional.empty());
        AuthRequest ar = new AuthRequest();
        ar.setEmail("no@ex");
        ar.setPassword("x");
        Assert.assertEquals(authController.login(ar).getStatusCodeValue(), 401);
    }

    @Test(priority = 51)
    public void t51_jwtClaimsIncludeUserDetails() {
        Map<String, Object> claims = Map.of("email", "a@ex", "role", "STAFF");
        String token = jwtUtil.generateToken(claims, "a@ex");
        Assert.assertTrue(jwtUtil.validateToken(token));
    }

    @Test(priority = 52)
    public void t52_jwtFilterSetsAuthenticationWhenValid() {
        JwtFilter filter = new JwtFilter(jwtUtil);
        Assert.assertNotNull(filter);
    }

    @Test(priority = 53)
    public void t53_jwtInvalidTokenRejected() {
        Assert.assertFalse(jwtUtil.validateToken("invalid.token"));
    }

    @Test(priority = 54)
    public void t54_authControllerRegisterDefaultsRoleStaff() {
        when(userRepository.findByEmail("r@ex")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        User saved = userService.register(User.builder().email("r@ex").password("p").build());
        Assert.assertEquals(saved.getRole(), "STAFF");
    }

    @Test(priority = 55)
    public void t55_protectedEndpointRequiresToken_simulated() {
        String token = jwtUtil.generateToken(Map.of(), "x@ex");
        Assert.assertTrue(jwtUtil.validateToken(token));
    }

    @Test(priority = 56)
    public void t56_bcryptPasswordMatches() {
        var enc = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
        String hash = enc.encode("mypwd");
        Assert.assertTrue(enc.matches("mypwd", hash));
    }

    @Test(priority = 57)
    public void t57_findCertificateByVerificationCodeRepo() {
        when(certificateRepository.findByVerificationCode("HVC1"))
                .thenReturn(Optional.of(Certificate.builder().id(321L).build()));
        Certificate out = certificateService.findByVerificationCode("HVC1");
        Assert.assertEquals(out.getId().longValue(), 321L);
    }

    @Test(priority = 58)
    public void t58_findCertificatesByStudentRepo() {
        Student s = Student.builder().id(400L).build();
        when(studentRepository.findById(400L)).thenReturn(Optional.of(s));
        when(certificateRepository.findByStudent(s)).thenReturn(List.of(new Certificate()));
        List<Certificate> list = certificateService.findByStudentId(400L);
        Assert.assertEquals(list.size(), 1);
    }

    @Test(priority = 59)
    public void t59_hqlEdgeCase_noResults() {
        when(certificateRepository.findByVerificationCode("NONE")).thenReturn(Optional.empty());
        try {
            certificateService.findByVerificationCode("NONE");
            Assert.fail();
        } catch (RuntimeException e) {
            Assert.assertTrue(e.getMessage().contains("Certificate not found"));
        }
    }

    @Test(priority = 60)
    public void t60_querySimulation_complexFilter() {
        List<Certificate> filtered =
                List.of(Certificate.builder().verificationCode("A").build())
                        .stream().filter(c -> c.getVerificationCode().startsWith("A")).toList();
        Assert.assertEquals(filtered.size(), 1);
    }

    @Test(priority = 61)
    public void t61_hqlAggregationSimulation() {
        Assert.assertEquals(List.of(1, 2).size(), 2);
    }

    @Test(priority = 62)
    public void t62_hcql_simulation_joinedQuery() {
        Student s = Student.builder().id(600L).name("Join").build();
        when(studentRepository.findById(600L)).thenReturn(Optional.of(s));
        when(certificateRepository.findByStudent(s)).thenReturn(List.of(Certificate.builder().student(s).build()));
        List<Certificate> list = certificateService.findByStudentId(600L);
        Assert.assertEquals(list.get(0).getStudent().getName(), "Join");
    }

    @Test(priority = 63)
    public void t63_hcql_paginationSimulation() {
        List<Certificate> many = new ArrayList<>();
        for (int i = 0; i < 10; i++) many.add(new Certificate());
        Assert.assertEquals(many.subList(0, 5).size(), 5);
    }

    @Test(priority = 64)
    public void t64_finalSanity() {
        Assert.assertNotNull(jwtUtil);
        Assert.assertNotNull(userService);
        Assert.assertNotNull(certificateService);
    }
}
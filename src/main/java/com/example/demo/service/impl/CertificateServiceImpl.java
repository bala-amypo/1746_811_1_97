@Service
public class CertificateServiceImpl implements CertificateService {

    private final CertificateRepository certRepo;
    private final StudentRepository studentRepo;
    private final CertificateTemplateRepository templateRepo;

    public CertificateServiceImpl(CertificateRepository c,
                                  StudentRepository s,
                                  CertificateTemplateRepository t) {
        this.certRepo = c;
        this.studentRepo = s;
        this.templateRepo = t;
    }

    @Override
    public Certificate generateCertificate(Long studentId, Long templateId) {
        Student s = studentRepo.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        CertificateTemplate t = templateRepo.findById(templateId)
                .orElseThrow(() -> new RuntimeException("Template not found"));

        Certificate c = Certificate.builder()
                .student(s)
                .template(t)
                .verificationCode("VC-" + UUID.randomUUID())
                .qrCodeUrl("data:image/png;base64,DUMMY")
                .issuedAt(LocalDateTime.now())
                .build();

        return certRepo.save(c);
    }

    @Override
    public Certificate getCertificate(Long id) {
        return certRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Certificate not found"));
    }

    @Override
    public Certificate findByVerificationCode(String code) {
        return certRepo.findByVerificationCode(code)
                .orElseThrow(() -> new RuntimeException("Certificate not found"));
    }

    @Override
    public List<Certificate> findByStudentId(Long studentId) {
        Student s = studentRepo.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        return certRepo.findByStudent(s);
    }
}

@Entity
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Certificate {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    private Student student;

    @ManyToOne
    private CertificateTemplate template;

    private String verificationCode;
    private String qrCodeUrl;
}

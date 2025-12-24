@Entity
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class CertificateTemplate {
    @Id @GeneratedValue
    private Long id;

    private String templateName;
    private String backgroundUrl;
}

@Entity
@Getter @Setter
public class VerificationLog {
    @Id @GeneratedValue
    private Long id;

    private String status;
    private String ipAddress;
    private LocalDateTime verifiedAt = LocalDateTime.now();
}

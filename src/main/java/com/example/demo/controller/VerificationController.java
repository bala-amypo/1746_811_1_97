@RestController
@RequestMapping("/api/verify")
public class VerificationController {

    private final VerificationService service;

    public VerificationController(VerificationService service) {
        this.service = service;
    }

    @GetMapping("/{code}")
    public Certificate verify(@PathVariable String code) {
        return service.verify(code);
    }
}

@RestController
@RequestMapping("/api/templates")
public class TemplateController {

    private final TemplateService service;

    public TemplateController(TemplateService service) {
        this.service = service;
    }

    @PostMapping
    public CertificateTemplate add(@RequestBody CertificateTemplate template) {
        return service.addTemplate(template);
    }

    @GetMapping
    public List<CertificateTemplate> list() {
        return service.getAll();
    }
}

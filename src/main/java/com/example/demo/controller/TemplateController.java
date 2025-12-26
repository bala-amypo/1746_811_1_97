@RestController
@RequestMapping("/templates")
public class TemplateController {

    private final TemplateService service;

    public TemplateController(TemplateService service) {
        this.service = service;
    }

    @PostMapping
    public CertificateTemplate add(@RequestBody CertificateTemplate t) {
        return service.addTemplate(t);
    }

    @GetMapping
    public List<CertificateTemplate> list() {
        return service.getAllTemplates();
    }
}

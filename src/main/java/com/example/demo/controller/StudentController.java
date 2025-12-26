@RestController
@RequestMapping("/students")
public class StudentController {

    private final StudentService service;

    public StudentController(StudentService service) {
        this.service = service;
    }

    @PostMapping
    public Student add(@RequestBody Student s) {
        return service.addStudent(s);
    }

    @GetMapping
    public List<Student> list() {
        return service.getAllStudents();
    }
}

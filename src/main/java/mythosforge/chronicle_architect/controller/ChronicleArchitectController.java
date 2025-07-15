package mythosforge.chronicle_architect.controller;

import mythosforge.chronicle_architect.models.Book;
import mythosforge.chronicle_architect.service.ChronicleArchitectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chronicle-architect/books")
public class ChronicleArchitectController {

    private final ChronicleArchitectService chronicleArchitectService;

    public ChronicleArchitectController(ChronicleArchitectService chronicleArchitectService) {
        this.chronicleArchitectService = chronicleArchitectService;
    }

    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody Book book) {
        return ResponseEntity.ok(chronicleArchitectService.createBook(book));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        return ResponseEntity.ok(chronicleArchitectService.findBookById(id));
    }

    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok(chronicleArchitectService.findAllBooks());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @RequestBody Book bookDetails) {
        return ResponseEntity.ok(chronicleArchitectService.updateBook(id, bookDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        chronicleArchitectService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}
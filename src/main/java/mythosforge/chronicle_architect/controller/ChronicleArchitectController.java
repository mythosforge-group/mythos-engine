package mythosforge.chronicle_architect.controller;

import mythosengine.core.engine.ContentGenerationEngine;
import mythosengine.spi.content.ContentGenerationContext;
import mythosengine.spi.content.GeneratedContent;
import mythosforge.chronicle_architect.models.Book;
import mythosforge.chronicle_architect.service.ChronicleArchitectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chronicle/books")
public class ChronicleArchitectController {

    private final ChronicleArchitectService chronicleArchitectService;
    private final ContentGenerationEngine generationEngine; 

    public ChronicleArchitectController(
        ChronicleArchitectService chronicleArchitectService,
        ContentGenerationEngine generationEngine
    ) {
        this.chronicleArchitectService = chronicleArchitectService;
        this.generationEngine = generationEngine;
    }

    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody Book book) {
        return ResponseEntity.ok(chronicleArchitectService.createBook(book));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        try {
            Book book = chronicleArchitectService.findBookById(id);
            return ResponseEntity.ok(book);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<BookResponseDTO>> getAllBooks() {
        return ResponseEntity.ok(chronicleArchitectService.findAllBooksAsDTO());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        chronicleArchitectService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
    

    /**
     * Aciona o módulo para sugerir uma estrutura de capítulos para um livro existente.
     */
    @PostMapping("/{bookId}/suggest-chapters")
    public ResponseEntity<String> suggestChapters(@PathVariable Long bookId) {
        Book book = chronicleArchitectService.findBookById(bookId);

        ContentGenerationContext context = ContentGenerationContext.builder()
            .generationType("STRUCTURE_CHAPTERS")
            .parameters(Map.of("book", book))
            .build();

        GeneratedContent result = generationEngine.process(context);        
        return ResponseEntity.ok(result.getMainText());
    }

    /**
     * Aciona o módulo para gerar um glossário com base na descrição de um livro.
     */
    @PostMapping("/{bookId}/suggest-glossary")
    public ResponseEntity<String> suggestGlossary(@PathVariable Long bookId) {
        Book book = chronicleArchitectService.findBookById(bookId);

        ContentGenerationContext context = ContentGenerationContext.builder()
            .generationType("GENERATE_GLOSSARY")
            .parameters(Map.of("book", book))
            .build();

        GeneratedContent result = generationEngine.process(context);

        return ResponseEntity.ok(result.getMainText());
    }
}
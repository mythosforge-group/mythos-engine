package mythosforge.fable_minds.controller;

import java.util.List;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;
import mythosforge.fable_minds.models.CharacterClass;
import mythosforge.fable_minds.service.CharacterClassService;

/* ENDPOINTS 
POST /api/classes - criar nova classe
GET /api/classes - listar todas
GET /api/classes/{id} - buscar por id
GET /api/classes/system/{systemId} - listar por sistema
PUT /api/classes/{id} - atualizar
DELETE /api/classes/{id} - excluir
*/

@RestController
@RequestMapping("/api/classes")
@Tag(name = "Character Class Controller", description = "CRUD de character classes")
public class CharacterClassController {

    private final CharacterClassService service;

    public CharacterClassController(CharacterClassService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<CharacterClass> create(@RequestBody CharacterClass characterClass) {
        return ResponseEntity.ok(service.create(characterClass));
    }

    @GetMapping
    public ResponseEntity<List<CharacterClass>> listAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CharacterClass> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/system/{systemId}")
    public ResponseEntity<List<CharacterClass>> listBySystem(@PathVariable Long systemId) {
        return ResponseEntity.ok(service.findBySystemId(systemId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CharacterClass> update(
            @PathVariable Long id,
            @RequestBody CharacterClass characterClass) {
        return ResponseEntity.ok(service.update(id, characterClass));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(404).body(ex.getMessage());
    }
}
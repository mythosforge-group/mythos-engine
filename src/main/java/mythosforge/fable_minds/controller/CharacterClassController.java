package mythosforge.fable_minds.controller;

import java.util.List;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;
import mythosforge.fable_minds.dto.CharacterClassDTO;
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
    public ResponseEntity<CharacterClassDTO> create(@RequestBody CharacterClass characterClass) {
        CharacterClass saved = service.create(characterClass);
        CharacterClassDTO dto = new CharacterClassDTO(
            saved.getId(), saved.getName(), saved.getDescription(), saved.getSystem().getId()
        );
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<List<CharacterClassDTO>> listAll() {
        return ResponseEntity.ok(service.findAllDto());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CharacterClassDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findByIdDto(id));
    }

    @GetMapping("/system/{systemId}")
    public ResponseEntity<List<CharacterClassDTO>> listBySystem(@PathVariable Long systemId) {
        return ResponseEntity.ok(service.findBySystemIdDto(systemId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CharacterClassDTO> update(
            @PathVariable Long id,
            @RequestBody CharacterClass characterClass) {
        CharacterClass updated = service.update(id, characterClass);
        CharacterClassDTO dto = new CharacterClassDTO(
            updated.getId(), updated.getName(), updated.getDescription(), updated.getSystem().getId()
        );
        return ResponseEntity.ok(dto);
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
package mythosforge.fable_minds.controller;

import java.util.List;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import mythosforge.fable_minds.models.CharacterDnd;
import mythosforge.fable_minds.service.CharacterDndService;

/* ENDPOINTS
POST /api/personagens/dnd – criar novo personagem DnD
GET /api/personagens/dnd – listar todos os personagens DnD
GET /api/personagens/dnd/{id} – buscar personagem DnD por ID
PUT /api/personagens/dnd/{id} – atualizar personagem DnD
DELETE /api/personagens/dnd/{id} – excluir personagem DnD
*/

@RestController
@RequestMapping("/api/personagens/dnd")
public class CharacterDndController {

    private final CharacterDndService characterDndService;

    public CharacterDndController(CharacterDndService characterDndService) {
        this.characterDndService = characterDndService;
    }

    @PostMapping
    public ResponseEntity<CharacterDnd> create(@RequestBody CharacterDnd p) {
        return ResponseEntity.ok(characterDndService.create(p));
    }

    @GetMapping
    public ResponseEntity<List<CharacterDnd>> listAll() {
        return ResponseEntity.ok(characterDndService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CharacterDnd> getById(@PathVariable Long id) {
        return ResponseEntity.ok(characterDndService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CharacterDnd> update(@PathVariable Long id, @RequestBody CharacterDnd p) {
        return ResponseEntity.ok(characterDndService.update(id, p));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        characterDndService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(404).body(ex.getMessage());
    }
}
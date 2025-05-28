package mythosforge.fable_minds.controller;

import java.util.List;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import mythosforge.fable_minds.models.Character;
import mythosforge.fable_minds.service.CharacterService;

/* ENDPOINTS
POST /api/personagens – criar novo personagem
GET /api/personagens – listar todos os personagens
GET /api/personagens/{id} – buscar personagem por ID
PUT /api/personagens/{id} – atualizar personagem
DELETE /api/personagens/{id} – excluir personagem
*/

@RestController
@RequestMapping("/api/personagens")
public class CharacterController {

    private final CharacterService characterService;

    public CharacterController(CharacterService characterService) {
        this.characterService = characterService;
    }

    @PostMapping
    public ResponseEntity<Character> create(@RequestBody Character p) {
        return ResponseEntity.ok(characterService.create(p));
    }

    @GetMapping
    public ResponseEntity<List<Character>> listAll() {
        return ResponseEntity.ok(characterService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Character> getById(@PathVariable Long id) {
        return ResponseEntity.ok(characterService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Character> update(@PathVariable Long id, @RequestBody Character p) {
        return ResponseEntity.ok(characterService.update(id, p));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        characterService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(404).body(ex.getMessage());
    }
}
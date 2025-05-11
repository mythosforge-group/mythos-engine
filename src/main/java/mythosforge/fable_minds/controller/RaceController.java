package mythosforge.fable_minds.controller;

import java.util.List;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;
import mythosforge.fable_minds.models.Race;
import mythosforge.fable_minds.service.RaceService;

/* ENDPOINTS 
POST /api/races - criar nova raça
GET /api/races - listar todas as raças
GET /api/races/{id} - buscar raça por ID
GET /api/races/system/{systemId} - listar raças de um sistema
PUT /api/races/{id} - atualizar raça
DELETE /api/races/{id} - excluir raça
*/

@RestController
@RequestMapping("/api/races")
@Tag(name = "Race Controller", description = "CRUD de raças de jogo")
public class RaceController {

    private final RaceService service;

    public RaceController(RaceService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Race> create(@RequestBody Race race) {
        return ResponseEntity.ok(service.create(race));
    }

    @GetMapping
    public ResponseEntity<List<Race>> listAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Race> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/system/{systemId}")
    public ResponseEntity<List<Race>> listBySystem(@PathVariable Long systemId) {
        return ResponseEntity.ok(service.findBySystemId(systemId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Race> update(
            @PathVariable Long id,
            @RequestBody Race race) {
        return ResponseEntity.ok(service.update(id, race));
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
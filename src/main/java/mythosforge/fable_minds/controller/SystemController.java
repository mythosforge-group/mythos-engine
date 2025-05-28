package mythosforge.fable_minds.controller;

import java.util.List;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;
import mythosforge.fable_minds.config.security.auhentication.dto.SystemDTO;
import mythosforge.fable_minds.models.System;
import mythosforge.fable_minds.service.SystemService;

/* ENDPOINTS
POST /api/systems - criar novo sistema de jogo
GET /api/systems - listar todos os sistemas de jogo
GET /api/systems/{id} - buscar sistema por ID
PUT /api/systems/{id} - atualizar sistema
DELETE /api/systems/{id} - excluir sistema 
*/

@RestController
@RequestMapping("/api/systems")
@Tag(name = "System Controller", description = "CRUD de sistemas de jogo")
public class SystemController {

    private final SystemService service;

    public SystemController(SystemService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<System> create(@RequestBody System system) {
        return ResponseEntity.ok(service.create(system));
    }

    @GetMapping
    public ResponseEntity<List<SystemDTO>> listAll() {
        return ResponseEntity.ok(service.findAllDto());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SystemDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findByIdDto(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<System> update(@PathVariable Long id,
                                         @RequestBody System system) {
        return ResponseEntity.ok(service.update(id, system));
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
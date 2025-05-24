package mythosforge.fable_minds.controller;

import jakarta.persistence.EntityNotFoundException;
import mythosforge.fable_minds.config.security.auhentication.dto.CharacterClassDTO;
import mythosforge.fable_minds.config.security.auhentication.dto.RaceDTO;
import mythosforge.fable_minds.config.security.auhentication.dto.SystemDTO;
import mythosforge.fable_minds.models.*;
import mythosforge.fable_minds.models.System;
import mythosforge.fable_minds.service.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/personagens/dnd")
public class CharacterGenerationController {

    private final CharacterGenerationService campaignService;
    private final CharacterDndService characterDndService;

    public CharacterGenerationController(
            CharacterGenerationService campaignService,
            CharacterDndService characterDndService
    ) {
        this.campaignService = campaignService;
        this.characterDndService = characterDndService;
    }

    @PostMapping("/gerar")
    public ResponseEntity<String> gerarFicha(
            @RequestParam Long campaignId,
            @RequestParam Long raceId,
            @RequestParam Long classId
    ) {
        String historia = campaignService.gerarFicha(campaignId, raceId, classId);
        return ResponseEntity.ok(historia);
    }

    @PostMapping("/gerar-e-salvar")
    public ResponseEntity<CharacterDnd> gerarFichaESalvar(
            @RequestParam Long campaignId,
            @RequestParam Long raceId,
            @RequestParam Long classId
    ) {
        CharacterDnd personagem = campaignService.gerarFichaESalvar(campaignId, raceId, classId);
        CharacterDnd salvo = characterDndService.create(personagem);
        return ResponseEntity.ok(salvo);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(404).body(ex.getMessage());
    }

    @PostMapping("/linhagem")
    public ResponseEntity<String> gerarArvoreGenealogica(@RequestParam Long characterId) {
        String linhagem = campaignService.gerarLinhagem(characterId);
        return ResponseEntity.ok(linhagem);
    }

}
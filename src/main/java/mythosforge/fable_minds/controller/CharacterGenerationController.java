package mythosforge.fable_minds.controller;

import jakarta.persistence.EntityNotFoundException;
import mythosforge.fable_minds.models.*;
import mythosforge.fable_minds.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.System;

@RestController
@RequestMapping("/api/personagens/dnd")
public class CharacterGenerationController {

    private final CharacterGenerationService campaignService;
    private final CharacterDndService characterDndService;
    private final ArvoreGenealogicaService arvoreGenealogicaService;

    public CharacterGenerationController(
            CharacterGenerationService campaignService,
            CharacterDndService characterDndService,
            ArvoreGenealogicaService arvoreGenealogicaService
    ) {
        this.campaignService = campaignService;
        this.characterDndService = characterDndService;
        this.arvoreGenealogicaService = arvoreGenealogicaService;
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



    @PostMapping("/linhagem")
    public ResponseEntity<String> gerarArvoreGenealogica(@RequestParam Long characterId) {
        String linhagem = campaignService.gerarLinhagem(characterId);
        System.out.println("JSON gerado: " + linhagem);

        arvoreGenealogicaService.salvarEstruturaGenealogica(characterId, linhagem);


        return ResponseEntity.ok(linhagem);
    }

    @GetMapping("/linhagem")
    public ResponseEntity<String> buscarEstruturaSalva(@RequestParam Long id) {
        String json = arvoreGenealogicaService.buscarEstruturaPorPersonagem(id);

        return ResponseEntity.ok(json);
    }


    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(404).body(ex.getMessage());
    }

}
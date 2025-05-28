package mythosforge.fable_minds.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import mythosforge.fable_minds.llm.tree.ArvoreGenealogicaGenerator;
import mythosforge.fable_minds.models.*;
import mythosforge.fable_minds.service.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.lang.System;
import java.nio.file.Files;

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
    public ResponseEntity<String> buscarEstruturaSalva(@RequestParam Long characterId) {
        String json = arvoreGenealogicaService.buscarEstruturaPorPersonagem(characterId);

        return ResponseEntity.ok(json);
    }



        @PostMapping("/linhagem/gerar-imagem")
        public ResponseEntity<byte[]> gerarGenealogia(@RequestParam Long characterId) throws Exception {
            String respostaJson = arvoreGenealogicaService.buscarEstruturaPorPersonagem(characterId);

            respostaJson = respostaJson
                    .replaceAll("(?s)^```json\\s*", "")  // remove início com ```json
                    .replaceAll("(?s)```\\s*$", "");     // remove final com ```
            ;

            ObjectMapper mapper = new ObjectMapper();

            Pessoa arvore = mapper.readValue(respostaJson, Pessoa.class);

            String outputPath = "/tmp/arvore.png";
            ArvoreGenealogicaGenerator.gerarImagemArvore(arvore, outputPath);


            File file = new File(outputPath);
            byte[] imagemBytes = Files.readAllBytes(file.toPath());

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(imagemBytes);
        }


    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(404).body(ex.getMessage());
    }
}
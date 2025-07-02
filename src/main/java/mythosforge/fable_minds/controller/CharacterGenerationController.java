package mythosforge.fable_minds.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import mythosengine.core.ContentGenerationEngine;
import mythosengine.core.modules.content.ContentGenerationContext;
import mythosengine.core.modules.content.GeneratedContent;
import mythosforge.fable_minds.llm.tree.ArvoreGenealogicaGenerator;
import mythosforge.fable_minds.models.*;
import mythosforge.fable_minds.service.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.lang.System;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/personagens/dnd")
public class CharacterGenerationController {

    private final CharacterGenerationService characterGenerationService;
    private final CharacterDndService characterDndService;
    private final ArvoreGenealogicaService arvoreGenealogicaService;
    private final ContentGenerationEngine generationEngine;
    private final CharacterClassService classService;
    private final RaceService raceService;
    private final CampaignService campaignService;

    public CharacterGenerationController(
        CharacterGenerationService characterGenerationService,
        CharacterDndService characterDndService,
        ArvoreGenealogicaService arvoreGenealogicaService,
        ContentGenerationEngine generationEngine,
        CharacterClassService classService,
        RaceService raceService,
        CampaignService campaignService
    ) {
        this.characterGenerationService = characterGenerationService;
        this.characterDndService = characterDndService;
        this.arvoreGenealogicaService = arvoreGenealogicaService;
        this.generationEngine = generationEngine;
        this.classService = classService;
        this.raceService = raceService;
        this.campaignService = campaignService;
    }

    @PostMapping("/gerar")
    public ResponseEntity<String> gerarFicha(
            @RequestParam Long campaignId,
            @RequestParam Long raceId,
            @RequestParam Long classId
    ) {
        String historia = characterGenerationService.gerarFicha(campaignId, raceId, classId);
        return ResponseEntity.ok(historia);
    }

    @PostMapping("/gerar-e-salvar")
    public ResponseEntity<CharacterDnd> gerarFichaESalvar(
            @RequestParam Long campaignId,
            @RequestParam Long raceId,
            @RequestParam Long classId
    ) {
        // 1. Montar o contexto
        Campaign campaign = campaignService.findById(campaignId);
        Race race = raceService.findById(raceId);
        CharacterClass characterClass = classService.findById(classId);

        Map<String, Object> params = new HashMap<>();
        params.put("campaign", campaign);
        params.put("race", race);
        params.put("class", characterClass);

        ContentGenerationContext context = ContentGenerationContext.builder()
                .generationType("HISTORIA_PERSONAGEM")
                .parameters(params)
                .build();

        // 2. Chamar o motor do framework
        GeneratedContent resultado = generationEngine.process(context);

        // 3. Usar o resultado para criar e salvar o personagem
        CharacterDnd personagem = new CharacterDnd();
        personagem.setHistoria(resultado.getMainText());
        personagem.setNome((String) resultado.getMetadata().get("nome"));

        // Preenchendo os campos restantes
        personagem.setNivel(1);
        personagem.setXp(0);
        personagem.setCampanha(campaign);
        personagem.setRaca(race);
        personagem.setCharacterClass(characterClass);

        // Rolando os atributos de D&D
        personagem.setForca(rolarAtributo());
        personagem.setDestreza(rolarAtributo());
        personagem.setConstituicao(rolarAtributo());
        personagem.setInteligencia(rolarAtributo());
        personagem.setSabedoria(rolarAtributo());
        personagem.setCarisma(rolarAtributo());

        CharacterDnd salvo = characterDndService.create(personagem);

        return ResponseEntity.ok(salvo);
    }

    // Método auxiliar para rolar atributos (4d6 drop lowest)
    private int rolarAtributo() {
        return 8 + (int)(Math.random() * 11); // Mantendo a sua lógica original por simplicidade
    }


    @PostMapping("/linhagem")
    public ResponseEntity<String> gerarArvoreGenealogica(@RequestParam Long characterId) {
        String linhagem = characterGenerationService.gerarLinhagem(characterId);
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
            arvoreGenealogicaService.salvarImagem(characterId, imagemBytes);

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(imagemBytes);
        }


    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(404).body(ex.getMessage());
    }
}

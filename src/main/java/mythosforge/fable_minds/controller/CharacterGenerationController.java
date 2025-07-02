package mythosforge.fable_minds.controller;


import jakarta.persistence.EntityNotFoundException;
import mythosengine.core.ContentGenerationEngine;
import mythosengine.core.modules.content.ContentGenerationContext;
import mythosengine.core.modules.content.GeneratedContent;
import mythosengine.services.lineage.LineageService;
import mythosengine.services.lineage.dto.GraphData;
import mythosengine.services.lineage.dto.TraversalDirection;
import mythosengine.services.lineage.visualizer.GraphVisualizer;
import mythosengine.services.lineage.visualizer.VisualizationOptions;

import mythosforge.fable_minds.models.*;
import mythosforge.fable_minds.service.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/personagens/dnd")
public class CharacterGenerationController {

    private final CharacterGenerationService characterGenerationService;
    private final CharacterDndService characterDndService;

    private final ContentGenerationEngine generationEngine;
    private final CharacterClassService classService;
    private final RaceService raceService;
    private final CampaignService campaignService;

    private final LineageService lineageService ;
    private final GraphVisualizer graphVisualizer;
    private final ArvoreGenealogicaService arvoreGenealogicaService;

    public CharacterGenerationController(
        CharacterGenerationService characterGenerationService,
        CharacterDndService characterDndService,
        ContentGenerationEngine generationEngine,
        CharacterClassService classService,
        RaceService raceService,
        CampaignService campaignService,
        LineageService lineageService,
        GraphVisualizer graphVisualizer,
        ArvoreGenealogicaService arvoreGenealogicaService

    ) {
        this.characterGenerationService = characterGenerationService;
        this.characterDndService = characterDndService;
        this.classService = classService;
        this.raceService = raceService;
        this.campaignService = campaignService;
        this.generationEngine = generationEngine;
        this.lineageService = lineageService;
        this.graphVisualizer = graphVisualizer;
        this.arvoreGenealogicaService = arvoreGenealogicaService;
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
        // Montar o contexto
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

        //Chamar o motor do framework
        GeneratedContent resultado = generationEngine.process(context);

        //Usar o resultado para criar e salvar o personagem
        CharacterDnd personagem = new CharacterDnd();
        personagem.setHistoria(resultado.getMainText());
        personagem.setNome((String) resultado.getMetadata().get("nome"));


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


    @PostMapping("/{characterId}/gerar-linhagem")
    public ResponseEntity<String> gerarLinhagem(@PathVariable Long characterId) {
        // Carrega o personagem da aplicação para usar como contexto
        CharacterDnd personagem = characterDndService.findById(characterId);

        // Monta o contexto para o motor do framework
        ContentGenerationContext context = ContentGenerationContext.builder()
                .generationType("GERAR_LINHAGEM")
                .parameters(Map.of("character", personagem))
                .build();

        // Chama o motor, que encontrará e executará o FamilyTreeGeneratorModule
        GeneratedContent resultado = generationEngine.process(context);

        return ResponseEntity.ok(resultado.getMainText() + " " + resultado.getCreatedEntities().size() + " novas entidades foram criadas.");
    }


    @GetMapping("/{characterId}/linhagem-imagem")
    public ResponseEntity<byte[]> gerarImagemGenealogia(@PathVariable Long characterId) throws Exception {

        UUID entityId = characterDndService.findById(characterId).getEntityId();

        // Chamar o LineageService do framework para construir o grafo genérico.
        // Navega "para cima" (UPSTREAM) a partir do personagem para encontrar seus pais, avós, etc.
        GraphData graphData = lineageService.buildGraph(
                entityId,
                "É_PAI_DE", // O tipo de relação que queremos seguir
                TraversalDirection.UPSTREAM,
                5 // Profundidade máxima de 5 gerações
        );

        // Chamar o GraphVisualizer do framework para renderizar a imagem.
        VisualizationOptions options = VisualizationOptions.builder()
                .outputFormat("png")
                .graphAttributes(Map.of("rankdir", "BT")) // BT = Bottom to Top, ideal para árvores genealógicas
                .build();

        byte[] imagemBytes = graphVisualizer.visualize(graphData, options);


        //arvoreGenealogicaService.salvarImagem(characterId, imagemBytes);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(imagemBytes);
    }


    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(404).body(ex.getMessage());
    }
}

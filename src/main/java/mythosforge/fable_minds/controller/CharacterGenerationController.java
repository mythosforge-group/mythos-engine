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

    public CharacterGenerationController(
        CharacterGenerationService characterGenerationService,
        CharacterDndService characterDndService,
        ContentGenerationEngine generationEngine,
        CharacterClassService classService,
        RaceService raceService,
        CampaignService campaignService,
        LineageService lineageService,
        GraphVisualizer graphVisualizer

    ) {
        this.characterGenerationService = characterGenerationService;
        this.characterDndService = characterDndService;
        this.classService = classService;
        this.raceService = raceService;
        this.campaignService = campaignService;
        this.generationEngine = generationEngine;
        this.lineageService = lineageService;
        this.graphVisualizer = graphVisualizer;
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

    /**
     * Endpoint para gerar uma imagem da árvore genealógica de um personagem.
     * Substitui o antigo POST /linhagem/gerar-imagem.
     */
    @GetMapping("/{characterId}/linhagem-imagem")
    public ResponseEntity<byte[]> gerarImagemGenealogia(@PathVariable Long characterId) throws Exception {
        // 1. Encontrar a Entidade do framework correspondente ao personagem da aplicação.
        //    (NOTA: Em um sistema real, você teria um mapeamento entre o ID da aplicação e o ID da entidade).
        //    Para este exemplo, vamos assumir que o characterDndService pode nos dar o UUID da entidade.
        UUID entityId = characterDndService.findById(characterId).getEntityId();

        // 2. Chamar o LineageService do framework para construir o grafo genérico.
        //    Navegamos "para cima" (UPSTREAM) a partir do personagem para encontrar seus pais, avós, etc.
        GraphData graphData = lineageService.buildGraph(
                entityId,
                "É_PAI_DE", // O tipo de relação que queremos seguir
                TraversalDirection.UPSTREAM,
                5 // Profundidade máxima de 5 gerações
        );

        // 3. Chamar o GraphVisualizer do framework para renderizar a imagem.
        VisualizationOptions options = VisualizationOptions.builder()
                .outputFormat("png")
                .graphAttributes(Map.of("rankdir", "BT")) // BT = Bottom to Top, ideal para árvores genealógicas
                .build();

        byte[] imagemBytes = graphVisualizer.visualize(graphData, options);

        // Opcional: Salvar a imagem gerada no banco de dados da aplicação
        // arvoreGenealogicaService.salvarImagem(characterId, imagemBytes);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(imagemBytes);
    }


    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(404).body(ex.getMessage());
    }
}

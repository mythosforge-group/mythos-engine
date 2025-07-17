package mythosforge.chronicle_architect.controller;

import mythosengine.spi.lineage.GraphVisualizer;
import mythosengine.spi.lineage.LineageService;
import mythosengine.spi.lineage.dto.GraphData;
import mythosengine.spi.lineage.dto.TraversalDirection;
import mythosengine.spi.lineage.dto.VisualizationOptions;
import mythosforge.chronicle_architect.models.RuleComponent;
import mythosforge.chronicle_architect.models.SkillComponent;
import mythosforge.chronicle_architect.service.RuleComponentService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/chronicle/rules")
public class RuleComponentController {

    private final RuleComponentService ruleComponentService;
    private final LineageService lineageService;
    private final GraphVisualizer graphVisualizer; // Injeta a interface do framework

    public RuleComponentController(RuleComponentService ruleComponentService,
                                 LineageService lineageService,
                                 GraphVisualizer graphVisualizer) { // O Spring injeta a implementação @Primary
        this.ruleComponentService = ruleComponentService;
        this.lineageService = lineageService;
        this.graphVisualizer = graphVisualizer;
    }

    @PostMapping("/skill")
    public ResponseEntity<SkillComponent> createSkill(@RequestBody SkillComponent skill) {
        return ResponseEntity.ok(ruleComponentService.create(skill));
    }

    @GetMapping
    public ResponseEntity<List<RuleComponent>> getAllRules() {
        return ResponseEntity.ok(ruleComponentService.findAll());
    }

    @PostMapping("/{skillId}/prerequisite/{prerequisiteId}")
    public ResponseEntity<Void> addPrerequisite(@PathVariable Long skillId, @PathVariable Long prerequisiteId) {
        ruleComponentService.addPrerequisite(skillId, prerequisiteId);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{ruleId}/dependency-graph", produces = "text/plain;charset=UTF-8")
    public ResponseEntity<byte[]> getDependencyGraph(@PathVariable Long ruleId) throws Exception {
        // 1. O Controller busca a entidade da aplicação.
        RuleComponent component = ruleComponentService.findById(ruleId);
        UUID entityId = component.getEntityId();

        // 2. O Controller usa o serviço do framework para obter os DADOS do grafo.
        GraphData graphData = lineageService.buildGraph(
                entityId,
                "HAS_PREREQUISITE",
                TraversalDirection.UPSTREAM,
                10 // Profundidade da busca
        );

        // 3. O Controller define as opções de visualização, pedindo um 'txt'.
        VisualizationOptions options = VisualizationOptions.builder()
                .outputFormat("txt") // <-- Especifica o formato que a implementação customizada suporta.
                .build();

        // 4. O Controller chama a implementação injetada para renderizar o resultado.
        byte[] asciiGraphBytes = graphVisualizer.visualize(graphData, options);

        // 5. Retorna a resposta com o Content-Type correto para texto.
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(asciiGraphBytes);
    }
}
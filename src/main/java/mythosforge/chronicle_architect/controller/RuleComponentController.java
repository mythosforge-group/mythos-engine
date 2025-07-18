package mythosforge.chronicle_architect.controller;

import mythosforge.chronicle_architect.models.RuleComponent;
import mythosforge.chronicle_architect.models.SkillComponent;
import mythosforge.chronicle_architect.service.RuleComponentService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import mythosengine.services.lineage.LineageService;
import mythosengine.spi.graph.GraphVisualizer;
import mythosengine.spi.graph.dto.GraphData;
import mythosengine.spi.graph.dto.TraversalDirection;
import mythosengine.spi.graph.dto.VisualizationOptions;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/chronicle/rules")
public class RuleComponentController {

    private final RuleComponentService ruleComponentService;
    private final LineageService lineageService;
    private final GraphVisualizer graphVisualizer;

    public RuleComponentController(RuleComponentService ruleComponentService,
                                 LineageService lineageService,
                                 GraphVisualizer graphVisualizer) {
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
        RuleComponent component = ruleComponentService.findById(ruleId);
        UUID entityId = component.getEntityId();

        GraphData graphData = lineageService.buildGraph(
                entityId,
                "HAS_PREREQUISITE",
                TraversalDirection.UPSTREAM,
                10 // Profundidade da busca
        );

        VisualizationOptions options = VisualizationOptions.builder()
                .outputFormat("txt")
                .build();

        byte[] asciiGraphBytes = graphVisualizer.visualize(graphData, options);

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(asciiGraphBytes);
    }
}
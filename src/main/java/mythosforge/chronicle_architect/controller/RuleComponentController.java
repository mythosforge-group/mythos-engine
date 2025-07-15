package mythosforge.chronicle_architect.controller;

import mythosengine.services.lineage.LineageService;
import mythosengine.services.lineage.dto.GraphData;
import mythosengine.services.lineage.dto.TraversalDirection;
import mythosengine.services.lineage.visualizer.GraphVisualizer;
import mythosengine.services.lineage.visualizer.VisualizationOptions;
import mythosforge.chronicle_architect.model.RuleComponent;
import mythosforge.chronicle_architect.model.SkillComponent;
import mythosforge.chronicle_architect.service.RuleComponentService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/architect/components")
public class RuleComponentController {

    private final RuleComponentService ruleComponentService;
    private final LineageService lineageService;
    private final GraphVisualizer graphVisualizer;

    public RuleComponentController(RuleComponentService ruleComponentService, LineageService lineageService, GraphVisualizer graphVisualizer) {
        this.ruleComponentService = ruleComponentService;
        this.lineageService = lineageService;
        this.graphVisualizer = graphVisualizer;
    }

    @PostMapping("/skill")
    public ResponseEntity<SkillComponent> createSkill(@RequestBody SkillComponent skill) {
        return ResponseEntity.ok(ruleComponentService.create(skill));
    }

    @PostMapping("/{componentId}/prerequisite/{prerequisiteId}")
    public ResponseEntity<Void> addPrerequisite(@PathVariable Long componentId, @PathVariable Long prerequisiteId) {
        ruleComponentService.addPrerequisite(componentId, prerequisiteId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/dependency-graph")
    public ResponseEntity<byte[]> getDependencyGraph(@PathVariable Long id) throws Exception {
        RuleComponent component = ruleComponentService.findById(id);
        UUID entityId = component.getEntityId();

        GraphData graphData = lineageService.buildGraph(
                entityId,
                "HAS_PREREQUISITE",
                TraversalDirection.UPSTREAM,
                10
        );

        VisualizationOptions options = VisualizationOptions.builder()
                .outputFormat("png")
                .graphAttributes(Map.of("rankdir", "BT")) // Bottom-to-Top
                .build();

        byte[] imageBytes = graphVisualizer.visualize(graphData, options);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(imageBytes);
    }
}
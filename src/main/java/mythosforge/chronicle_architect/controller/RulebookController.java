package mythosforge.chronicle_architect.controller;

import mythosengine.core.ContentGenerationEngine;
import mythosengine.core.modules.content.ContentGenerationContext;
import mythosengine.core.modules.content.GeneratedContent;
import mythosforge.chronicle_architect.model.Rulebook;
import mythosforge.chronicle_architect.service.RulebookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/architect/rulebooks")
public class RulebookController {

    private final RulebookService rulebookService;
    private final ContentGenerationEngine generationEngine;

    public RulebookController(RulebookService rulebookService, ContentGenerationEngine generationEngine) {
        this.rulebookService = rulebookService;
        this.generationEngine = generationEngine;
    }

    @PostMapping
    public ResponseEntity<Rulebook> createRulebook(@RequestBody Rulebook rulebook) {
        return ResponseEntity.ok(rulebookService.create(rulebook));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rulebook> getRulebookById(@PathVariable Long id) {
        return ResponseEntity.ok(rulebookService.findById(id));
    }

    @PostMapping("/{id}/suggest-chapters")
    public ResponseEntity<String> suggestChapters(@PathVariable Long id) {
        Rulebook rulebook = rulebookService.findById(id);

        ContentGenerationContext context = ContentGenerationContext.builder()
                .generationType("BOOK_SUGGEST_CHAPTERS")
                .parameters(Map.of("rulebook", rulebook))
                .build();

        GeneratedContent result = generationEngine.process(context);
        return ResponseEntity.ok(result.getMainText());
    }
}
package mythosforge.lore_weaver.controller;



import mythosforge.lore_weaver.models.LoreArticle;
import mythosforge.lore_weaver.services.LoreArticleService;
import jakarta.persistence.EntityNotFoundException;
import mythosengine.core.engine.ContentGenerationEngine;
import mythosengine.spi.content.ContentGenerationContext;
import mythosengine.spi.content.GeneratedContent;
import mythosengine.spi.lineage.GraphVisualizer;
import mythosengine.spi.lineage.LineageService;
import mythosengine.spi.lineage.dto.GraphData;
import mythosengine.spi.lineage.dto.TraversalDirection;
import mythosengine.spi.lineage.dto.VisualizationOptions;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/lore/articles")
public class LoreArticleController {

    // --- Serviços da Aplicação Lore Weaver ---
    private final LoreArticleService loreArticleService;

    // --- Serviços do Framework Mythos Engine ---
    private final ContentGenerationEngine generationEngine;
    private final LineageService lineageService;
    private final GraphVisualizer graphVisualizer;

    public LoreArticleController(LoreArticleService loreArticleService,
                                 ContentGenerationEngine generationEngine,
                                 LineageService lineageService,
                                 GraphVisualizer graphVisualizer) {
        this.loreArticleService = loreArticleService;
        this.generationEngine = generationEngine;
        this.lineageService = lineageService;
        this.graphVisualizer = graphVisualizer;
    }

    // === Endpoints CRUD Básicos ===

    @PostMapping
    public ResponseEntity<LoreArticle> createArticle(@RequestBody LoreArticle article) {
        LoreArticle createdArticle = loreArticleService.create(article);
        return ResponseEntity.ok(createdArticle);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoreArticle> getArticleById(@PathVariable Long id) {
        LoreArticle article = loreArticleService.findById(id);
        return ResponseEntity.ok(article);
    }

    @GetMapping
    public ResponseEntity<List<LoreArticle>> getAllArticles() {
        List<LoreArticle> articles = loreArticleService.findAll();
        return ResponseEntity.ok(articles);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LoreArticle> updateArticle(@PathVariable Long id, @RequestBody LoreArticle articleDetails) {
        LoreArticle updatedArticle = loreArticleService.update(id, articleDetails);
        return ResponseEntity.ok(updatedArticle);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id) {
        loreArticleService.delete(id);
        return ResponseEntity.noContent().build();
    }





    @PostMapping("/{id}/expand")
    public ResponseEntity<LoreArticle> expandArticleContent(@PathVariable Long id) {

        LoreArticle article = loreArticleService.findById(id);


        ContentGenerationContext context = ContentGenerationContext.builder()
                .generationType("EXPAND_ARTICLE")
                .parameters(Map.of("article", article))
                .build();


        GeneratedContent result = generationEngine.process(context);


        article.setHistoria(result.getMainText());


        LoreArticle updatedArticle = loreArticleService.expandHistory(id, article.getHistoria());


        return ResponseEntity.ok(updatedArticle);
    }

    @PostMapping("/{id}/suggest-connections")
    public ResponseEntity<GeneratedContent> suggestConnections(@PathVariable Long id) {
        LoreArticle sourceArticle = loreArticleService.findById(id);

        ContentGenerationContext context = ContentGenerationContext.builder()
                .generationType("SUGGEST_CONNECTIONS")
                .parameters(Map.of("sourceArticle", sourceArticle))
                .build();

        GeneratedContent result = generationEngine.process(context);
        return ResponseEntity.ok(result);



    }

    @GetMapping("/{id}/graph")
    public ResponseEntity<byte[]> getConnectionsGraph(
            @PathVariable Long id,
            @RequestParam(defaultValue = "DOWNSTREAM") String direction,
            @RequestParam(defaultValue = "5") int depth
    ) throws Exception {

        LoreArticle sourceArticle = loreArticleService.findById(id);
        UUID entityId = sourceArticle.getEntityId();

        TraversalDirection traversalDirection = "UPSTREAM".equalsIgnoreCase(direction)
                ? TraversalDirection.UPSTREAM
                : TraversalDirection.DOWNSTREAM;

        GraphData graphData = lineageService.buildGraph(
                entityId,
                null, // Passando null para seguir TODOS os tipos de relação
                traversalDirection,
                depth
        );

        VisualizationOptions options = VisualizationOptions.builder()
                .outputFormat("png")
                .graphAttributes(Map.of("rankdir", "LR")) // LR = Left to Right
                .build();

        byte[] imageBytes = graphVisualizer.visualize(graphData, options);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(imageBytes);
    }

    @ExceptionHandler({EntityNotFoundException.class, IllegalStateException.class})
    public ResponseEntity<String> handleNotFound(RuntimeException ex) {
        return ResponseEntity.status(404).body(ex.getMessage());
    }
}


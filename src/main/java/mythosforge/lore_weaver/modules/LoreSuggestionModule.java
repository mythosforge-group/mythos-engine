package mythosforge.lore_weaver.modules;



import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import mythosforge.lore_weaver.models.LoreArticle;
import mythosforge.lore_weaver.modules.dto.SuggestedConnectionDTO;
import mythosforge.lore_weaver.services.LoreArticleService;
import mythosengine.core.entity.Entity;
import mythosengine.core.modules.content.ContentGenerationContext;
import mythosengine.core.modules.content.GeneratedContent;
import mythosengine.core.modules.content.IContentGeneratorModule;
import mythosengine.core.persistence.PersistencePort;
import mythosforge.lore_weaver.llm.LlmClientServiceLore;
import mythosforge.lore_weaver.llm.PromptBuilderLore;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class LoreSuggestionModule implements IContentGeneratorModule {

    private final LlmClientServiceLore llmClient;
    private final LoreArticleService loreArticleService;
    private final PersistencePort persistencePort;
    private final ObjectMapper objectMapper;

    public LoreSuggestionModule(LlmClientServiceLore llmClient,
                                LoreArticleService loreArticleService,
                                PersistencePort persistencePort) {
        this.llmClient = llmClient;
        this.loreArticleService = loreArticleService;
        this.persistencePort = persistencePort;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public boolean supports(ContentGenerationContext context) {
        return "SUGGEST_CONNECTIONS".equals(context.getGenerationType());
    }

    @Override
    public GeneratedContent generate(ContentGenerationContext context) {
        LoreArticle sourceArticle = (LoreArticle) context.getParameters().get("sourceArticle");

        String prompt = PromptBuilderLore.buildLoreConnectionPrompt(sourceArticle.getNome(), sourceArticle.getHistoria());
        String llmResponse = llmClient.request(prompt);
        String llmResponseclean = extractJsonArray(llmResponse);

        try {
            List<SuggestedConnectionDTO> suggestions = objectMapper.readValue(llmResponseclean, new TypeReference<>() {});
            List<Entity> createdEntities = new ArrayList<>();

            Entity sourceFrameworkEntity = persistencePort.findById(sourceArticle.getEntityId())
                    .orElseThrow(() -> new IllegalStateException("Entidade do framework não encontrada para o artigo fonte."));

            for (SuggestedConnectionDTO suggestion : suggestions) {
                LoreArticle newArticle = new LoreArticle();
                newArticle.setNome(suggestion.getNome());
                newArticle.setArticleType(suggestion.getTipo());
                newArticle.setSummary(suggestion.getResumo());
                newArticle.setHistoria(""); // Inicia com corpo vazio


                LoreArticle savedArticle = loreArticleService.create(newArticle);
                createdEntities.add(persistencePort.findById(savedArticle.getEntityId()).get());

                sourceFrameworkEntity.addRelationship(savedArticle.getEntityId(), suggestion.getTipoRelacao());
            }

            persistencePort.save(sourceFrameworkEntity);

            return GeneratedContent.builder()
                    .mainText(suggestions.size() + " novas conexões sugeridas e criadas com sucesso.")
                    .createdEntities(createdEntities)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Falha ao processar sugestões da LLM.", e);
        }
    }


    private static String extractJsonArray(String rawResponse) {
        int firstBracket = rawResponse.indexOf('[');
        int lastBracket = rawResponse.lastIndexOf(']');

        if (firstBracket != -1 && lastBracket != -1 && lastBracket > firstBracket) {
            return rawResponse.substring(firstBracket, lastBracket + 1);
        }

        // Retorna null se não conseguir encontrar um array JSON válido
        return null;
    }

    @Override
    public String getModuleName() {
        return "Lore Weaver Suggestion Module";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }
}


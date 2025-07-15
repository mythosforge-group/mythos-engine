package mythosforge.lore_weaver.modules;



import mythosengine.core.modules.content.ContentGenerationContext;
import mythosengine.core.modules.content.GeneratedContent;
import mythosengine.core.modules.content.IContentGeneratorModule;
import mythosengine.core.template.GenericTemplateService;
import mythosengine.core.template.RpgTemplate;
import mythosengine.services.llm.LlmClientServiceLore;
import mythosforge.fable_minds.llm.ResponseParser;

import mythosforge.lore_weaver.models.LoreArticle;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class LoreArticleGeneratorModule implements IContentGeneratorModule {

    private final LlmClientServiceLore llmClient;
    private final GenericTemplateService templateService;

    public LoreArticleGeneratorModule(LlmClientServiceLore llmClient, GenericTemplateService templateService) {
        this.llmClient = llmClient;
        this.templateService = templateService;
    }

    @Override
    public boolean supports(ContentGenerationContext context) {
        // Este módulo só funciona se o contexto contiver um LoreArticle
        // e se o tipo de geração for para expandir.
        if (!context.getParameters().containsKey("article")) {
            return false;
        }
        return "EXPAND_ARTICLE".equals(context.getGenerationType());
    }

    @Override
    public GeneratedContent generate(ContentGenerationContext context) {
        LoreArticle article = (LoreArticle) context.getParameters().get("article");

        RpgTemplate template = templateService.getTemplate("lore_weaver", "expand_article");

        Map<String, Object> templateData = Map.of(
                "article.name", article.getNome(),
                "article.content", article.getHistoria()
        );

        String finalPrompt = templateService.processTemplate(template, templateData);
        String llmRawResponse = llmClient.request(finalPrompt);

        String cleanedText = ResponseParser.extractContentAfterThinkBlock(llmRawResponse);

        return GeneratedContent.builder()
                .mainText(cleanedText) // Retorna o texto expandido
                .build();
    }

    @Override
    public String getModuleName() {
        return "Lore Weaver Article Expander";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }
}


package mythosforge.lore_weaver.modules;

import mythosengine.services.llm.GeminiClientService;
import mythosengine.spi.content.ContentGenerationContext;
import mythosengine.spi.content.GeneratedContent;
import mythosengine.spi.content.IContentGeneratorModule;
import mythosengine.spi.prompt.PromptResolver;
import mythosforge.fable_minds.llm.ResponseParser;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LoreArticleGeneratorModule implements IContentGeneratorModule {

    private final GeminiClientService llmClient;
    private final List<PromptResolver> promptResolvers;

    public LoreArticleGeneratorModule(GeminiClientService llmClient, List<PromptResolver> promptResolvers) {
        this.llmClient = llmClient;
        this.promptResolvers = promptResolvers;
    }

    @Override
    public boolean supports(ContentGenerationContext context) {
        // Este módulo agora pode suportar qualquer contexto que um de seus resolvers suporte.
        return "EXPAND_ARTICLE".equals(context.getGenerationType()) &&
               promptResolvers.stream().anyMatch(r -> r.supports(context));
    }

    @Override
    public GeneratedContent generate(ContentGenerationContext context) {
        PromptResolver resolver = promptResolvers.stream()
            .filter(r -> r.supports(context))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Nenhum PromptResolver para EXPAND_ARTICLE."));

        String finalPrompt = resolver.resolve(context);
        String llmRawResponse = llmClient.generateContent(finalPrompt);
        String cleanedText = ResponseParser.extractContentAfterThinkBlock(llmRawResponse);

        return GeneratedContent.builder()
                .mainText(cleanedText)
                .build();
    }

    @Override
    public String getModuleName() {
        return "Lore Weaver Article Expander";
    }

    @Override
    public String getVersion() {
        return "2.0.0";
    }
}
package mythosforge.lore_weaver.modules;

import mythosengine.services.llm.GeminiClientService;
import mythosengine.spi.content.ContentGenerationContext;
import mythosengine.spi.content.GeneratedContent;
import mythosengine.spi.content.IContentGeneratorModule;
import mythosengine.spi.prompt.PromptBuilder;
import mythosforge.fable_minds.llm.ResponseParser;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LoreArticleGeneratorModule implements IContentGeneratorModule {

    private final GeminiClientService llmClient;
    private final List<PromptBuilder> promptBuilders;

    public LoreArticleGeneratorModule(GeminiClientService llmClient, List<PromptBuilder> promptBuilders) {
        this.llmClient = llmClient;
        this.promptBuilders = promptBuilders;
    }

    @Override
    public boolean supports(ContentGenerationContext context) {
        return "EXPAND_ARTICLE".equals(context.getGenerationType());
    }

    @Override
    public GeneratedContent generate(ContentGenerationContext context) {
        PromptBuilder builder = promptBuilders.stream()
            .filter(b -> b.supports(context))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Nenhum PromptBuilder para 'EXPAND_ARTICLE' encontrado."));

        String finalPrompt = builder.build(context);
        String llmRawResponse = llmClient.generateContent(finalPrompt);
        String cleanedText = ResponseParser.extractContentAfterThinkBlock(llmRawResponse);

        return GeneratedContent.builder()
                .mainText(cleanedText)
                .build();
    }

    @Override
    public String getModuleName() { return "Lore Weaver Article Expander"; }
    @Override
    public String getVersion() { return "3.0.0"; }
}
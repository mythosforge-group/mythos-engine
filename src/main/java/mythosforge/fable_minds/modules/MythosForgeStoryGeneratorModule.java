package mythosforge.fable_minds.modules;

import mythosengine.services.llm.GeminiClientService;
import mythosengine.spi.content.ContentGenerationContext;
import mythosengine.spi.content.GeneratedContent;
import mythosengine.spi.content.IContentGeneratorModule;
import mythosengine.spi.prompt.PromptBuilder;
import mythosforge.fable_minds.llm.ResponseParser;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class MythosForgeStoryGeneratorModule implements IContentGeneratorModule {

    private final GeminiClientService llmClient;
    private final List<PromptBuilder> promptBuilders;

    public MythosForgeStoryGeneratorModule(GeminiClientService llmClient, List<PromptBuilder> promptBuilders) {
        this.llmClient = llmClient;
        this.promptBuilders = promptBuilders;
    }

    @Override
    public boolean supports(ContentGenerationContext context) {
        return "HISTORIA_PERSONAGEM".equals(context.getGenerationType());
    }

    @Override
    public GeneratedContent generate(ContentGenerationContext context) {
        PromptBuilder builder = promptBuilders.stream()
                .filter(b -> b.supports(context))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Nenhum PromptBuilder compatível encontrado para o tipo: " + context.getGenerationType()));

        String finalPrompt = builder.build(context);
        String llmResponse = llmClient.generateContent(finalPrompt);

        String historia = ResponseParser.extrairHistoriaLimpa(llmResponse);
        String nome = ResponseParser.extrairNome(llmResponse);

        return GeneratedContent.builder()
                .mainText(historia)
                .metadata(Map.of("nome", nome))
                .build();
    }

    @Override
    public String getModuleName() {
        return "MythosForge Story Generator";
    }

    @Override
    public String getVersion() {
        return "3.0.0";
    }
}
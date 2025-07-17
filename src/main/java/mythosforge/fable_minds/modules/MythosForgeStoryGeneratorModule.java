package mythosforge.fable_minds.modules;

import mythosengine.services.llm.GeminiClientService;
import mythosengine.spi.content.ContentGenerationContext;
import mythosengine.spi.content.GeneratedContent;
import mythosengine.spi.content.IContentGeneratorModule;
import mythosengine.spi.prompt.PromptResolver; // <-- USA A ABSTRAÇÃO
import mythosforge.fable_minds.llm.ResponseParser;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class MythosForgeStoryGeneratorModule implements IContentGeneratorModule {

    private final GeminiClientService llmClient;
    private final List<PromptResolver> promptResolvers; // <-- Injeta uma lista de todas as implementações disponíveis

    public MythosForgeStoryGeneratorModule(GeminiClientService llmClient, List<PromptResolver> promptResolvers) {
        this.llmClient = llmClient;
        this.promptResolvers = promptResolvers;
    }

    @Override
    public boolean supports(ContentGenerationContext context) {
        // Delega a responsabilidade para os resolvers. Se algum deles suportar, o módulo também suporta.
        return promptResolvers.stream().anyMatch(resolver -> resolver.supports(context));
    }

    @Override
    public GeneratedContent generate(ContentGenerationContext context) {
        PromptResolver resolver = promptResolvers.stream()
                .filter(r -> r.supports(context))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Nenhum PromptResolver compatível encontrado para o contexto."));

        String finalPrompt = resolver.resolve(context);

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
        return "2.0.0";
    }
}
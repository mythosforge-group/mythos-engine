package mythosforge.chronicle_architect.modules;

import mythosengine.services.llm.GeminiClientService;
import mythosengine.spi.content.ContentGenerationContext;
import mythosengine.spi.content.GeneratedContent;
import mythosengine.spi.content.IContentGeneratorModule;
import mythosengine.spi.prompt.PromptBuilder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GlossaryGenerationModule implements IContentGeneratorModule {

    private final GeminiClientService llmClient;
    private final List<PromptBuilder> promptBuilders;

    public GlossaryGenerationModule(GeminiClientService llmClient, List<PromptBuilder> promptBuilders) {
        this.llmClient = llmClient;
        this.promptBuilders = promptBuilders;
    }

    @Override
    public boolean supports(ContentGenerationContext context) {
        return "GENERATE_GLOSSARY".equals(context.getGenerationType());
    }

    @Override
    public GeneratedContent generate(ContentGenerationContext context) {
        PromptBuilder builder = promptBuilders.stream()
            .filter(b -> b.supports(context))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Nenhum PromptBuilder para 'GENERATE_GLOSSARY' encontrado."));

        String prompt = builder.build(context);
        String glossaryContent = llmClient.generateContent(prompt);
        return GeneratedContent.builder().mainText(glossaryContent).build();
    }

    @Override
    public String getModuleName() {
        return "Glossary Generator";
    }

    @Override
    public String getVersion() {
        return "3.0.0";
    }
}
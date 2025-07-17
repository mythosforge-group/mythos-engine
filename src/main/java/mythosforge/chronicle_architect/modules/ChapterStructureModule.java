package mythosforge.chronicle_architect.modules;

import mythosengine.services.llm.GeminiClientService;
import mythosengine.spi.content.ContentGenerationContext;
import mythosengine.spi.content.GeneratedContent;
import mythosengine.spi.content.IContentGeneratorModule;
import mythosengine.spi.prompt.PromptBuilder;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class ChapterStructureModule implements IContentGeneratorModule {

    private final GeminiClientService llmClient;
    private final List<PromptBuilder> promptBuilders;

    public ChapterStructureModule(GeminiClientService llmClient, List<PromptBuilder> promptBuilders) {
        this.llmClient = llmClient;
        this.promptBuilders = promptBuilders;
    }

    @Override
    public boolean supports(ContentGenerationContext context) {
        return "STRUCTURE_CHAPTERS".equals(context.getGenerationType());
    }

    @Override
    public GeneratedContent generate(ContentGenerationContext context) {
        PromptBuilder builder = promptBuilders.stream()
            .filter(b -> b.supports(context))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Nenhum PromptBuilder para 'STRUCTURE_CHAPTERS' encontrado."));
        
        String prompt = builder.build(context);
        String chapterList = llmClient.generateContent(prompt);
        return GeneratedContent.builder().mainText(chapterList).build();
    }

    @Override
    public String getModuleName() { return "Chapter Structure Generator"; }
    @Override
    public String getVersion() { return "3.0.0"; }
}
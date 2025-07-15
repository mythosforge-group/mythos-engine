package mythosforge.chronicle_architect.modules;

import mythosengine.core.modules.content.ContentGenerationContext;
import mythosengine.core.modules.content.GeneratedContent;
import mythosengine.core.modules.content.IContentGeneratorModule;
import mythosengine.core.template.GenericTemplateService;
import mythosforge.chronicle_architect.llm.LlmClientServiceArchitect;
import mythosforge.chronicle_architect.llm.PromptBuilderArchitect;
import mythosforge.chronicle_architect.llm.ResponseParserArchitect;
import mythosforge.chronicle_architect.model.Rulebook;
import mythosforge.chronicle_architect.model.SpellComponent;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BookStructureModule implements IContentGeneratorModule {

    // --- DEPENDÊNCIAS CORRIGIDAS ---
    private final LlmClientServiceArchitect llmClient;
    private final GenericTemplateService templateService;
    private static final List<String> SUPPORTED_TYPES = List.of("BOOK_SUGGEST_CHAPTERS", "BOOK_GENERATE_SPELL");

    // Injetando o novo LlmClientServiceArchitect
    public BookStructureModule(LlmClientServiceArchitect llmClient, GenericTemplateService templateService) {
        this.llmClient = llmClient;
        this.templateService = templateService;
    }

    @Override
    public boolean supports(ContentGenerationContext context) {
        return SUPPORTED_TYPES.contains(context.getGenerationType());
    }

    @Override
    public GeneratedContent generate(ContentGenerationContext context) {
        switch (context.getGenerationType()) {
            case "BOOK_SUGGEST_CHAPTERS":
                return generateChapterSuggestions(context);
            case "BOOK_GENERATE_SPELL":
                return generateSpellDescription(context);
            default:
                throw new UnsupportedOperationException("Tipo de geração não suportado: " + context.getGenerationType());
        }
    }

    private GeneratedContent generateChapterSuggestions(ContentGenerationContext context) {
        Rulebook rulebook = (Rulebook) context.getParameters().get("rulebook");

        // Usando o novo PromptBuilderArchitect
        String prompt = PromptBuilderArchitect.buildChapterSuggestionPrompt(templateService, rulebook);
        String rawResponse = llmClient.request(prompt);
        String cleanedResponse = ResponseParserArchitect.cleanResponse(rawResponse);

        return GeneratedContent.builder().mainText(cleanedResponse).build();
    }

    private GeneratedContent generateSpellDescription(ContentGenerationContext context) {
        SpellComponent spell = (SpellComponent) context.getParameters().get("spell");

        // Usando o novo PromptBuilderArchitect
        String prompt = PromptBuilderArchitect.buildSpellGenerationPrompt(templateService, spell);
        String rawResponse = llmClient.request(prompt);
        String cleanedResponse = ResponseParserArchitect.cleanResponse(rawResponse);

        return GeneratedContent.builder().mainText(cleanedResponse).build();
    }

    @Override
    public String getModuleName() {
        return "Chronicle Architect - Book Structure Module";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }
}
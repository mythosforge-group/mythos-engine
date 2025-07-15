package mythosforge.chronicle_architect.modules;

import org.springframework.stereotype.Component;

import mythosengine.core.modules.content.ContentGenerationContext;
import mythosengine.core.modules.content.GeneratedContent;
import mythosengine.core.modules.content.IContentGeneratorModule;
import mythosengine.services.llm.GeminiClientService;
import mythosforge.chronicle_architect.models.Book;

@Component
public class ChapterStructureModule implements IContentGeneratorModule {

    private final GeminiClientService geminiClient;

    public ChapterStructureModule(GeminiClientService geminiClient) {
        this.geminiClient = geminiClient;
    }

    @Override
    public boolean supports(ContentGenerationContext context) {
        return "STRUCTURE_CHAPTERS".equals(context.getGenerationType());
    }

    @Override
    public GeneratedContent generate(ContentGenerationContext context) {
        Book book = (Book) context.getParameters().get("book");
        if (book == null) {
            return GeneratedContent.builder().mainText("Erro: Livro não fornecido no contexto.").build();
        }

        String prompt = String.format(
            "Aja como um editor de livros de RPG experiente. Para um livro com o título '%s' e a seguinte descrição: '%s', " +
            "sugira uma lista estruturada de títulos de capítulos. A lista deve ser apenas os títulos, um por linha.",
            book.getTitle(), book.getDescription()
        );

        String chapterList = geminiClient.generateContent(prompt);
        return GeneratedContent.builder().mainText(chapterList).build();
    }

    @Override
    public String getModuleName() {
        return "Chapter Structure Generator (Gemini)";
    }

    @Override
    public String getVersion() {
        return "1.1.0";
    }
}
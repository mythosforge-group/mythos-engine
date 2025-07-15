package mythosforge.chronicle_architect.modules;

import mythosengine.core.modules.content.ContentGenerationContext;
import mythosengine.core.modules.content.GeneratedContent;
import mythosengine.core.modules.content.IContentGeneratorModule;
import mythosengine.services.llm.GeminiClientService; // Importa o novo serviço
import mythosforge.chronicle_architect.models.Book;
import org.springframework.stereotype.Component;

@Component
public class GlossaryGenerationModule implements IContentGeneratorModule {

    private final GeminiClientService geminiClient; // Injeta o serviço do Gemini

    public GlossaryGenerationModule(GeminiClientService geminiClient) {
        this.geminiClient = geminiClient;
    }

    @Override
    public boolean supports(ContentGenerationContext context) {
        return "GENERATE_GLOSSARY".equals(context.getGenerationType());
    }

    @Override
    public GeneratedContent generate(ContentGenerationContext context) {
        Book book = (Book) context.getParameters().get("book");
        if (book == null || book.getDescription() == null || book.getDescription().isBlank()) {
            return GeneratedContent.builder().mainText("Erro: Conteúdo do livro não fornecido para gerar o glossário.").build();
        }

        String prompt = String.format(
            "Analise o seguinte texto de um livro de RPG: '%s'. " +
            "Extraia de 5 a 10 termos chave que seriam úteis em um glossário. " +
            "Retorne a lista no formato 'Termo: Definição concisa.', com um termo por linha.",
            book.getDescription()
        );

        String glossaryContent = geminiClient.generateContent(prompt);
        return GeneratedContent.builder().mainText(glossaryContent).build();
    }

    @Override
    public String getModuleName() {
        return "Glossary Generator (Gemini)";
    }

    @Override
    public String getVersion() {
        return "1.1.0";
    }
}
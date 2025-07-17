package mythosforge.chronicle_architect.modules;

import mythosforge.chronicle_architect.llm.LlmClientServiceArchitect;
import mythosforge.chronicle_architect.models.Book;
import org.springframework.stereotype.Component;

import mythosengine.spi.content.ContentGenerationContext;
import mythosengine.spi.content.GeneratedContent;
import mythosengine.spi.content.IContentGeneratorModule;

@Component
public class GlossaryGenerationModule implements IContentGeneratorModule {

    private final LlmClientServiceArchitect llmClient;

    public GlossaryGenerationModule(LlmClientServiceArchitect llmClient) {
        this.llmClient = llmClient;
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

        String glossaryContent = llmClient.generateContent(prompt); 
        return GeneratedContent.builder().mainText(glossaryContent).build();
    }

    @Override
    public String getModuleName() {
        return "Glossary Generator";
    }

    @Override
    public String getVersion() {
        return "1.2.0";
    }
}
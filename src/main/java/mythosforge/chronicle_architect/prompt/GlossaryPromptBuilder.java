package mythosforge.chronicle_architect.prompt;

import mythosengine.spi.content.ContentGenerationContext;
import mythosengine.spi.prompt.PromptBuilder;
import mythosforge.chronicle_architect.models.Book;
import org.springframework.stereotype.Component;

@Component
public class GlossaryPromptBuilder implements PromptBuilder {

    @Override
    public boolean supports(ContentGenerationContext context) {
        return "GENERATE_GLOSSARY".equals(context.getGenerationType());
    }

    @Override
    public String build(ContentGenerationContext context) {
        Book book = (Book) context.getParameters().get("book");
        if (book == null || book.getDescription() == null || book.getDescription().isBlank()) {
            throw new IllegalStateException("Conteúdo do livro não fornecido para gerar o glossário.");
        }

        return String.format(
            "Analise o seguinte texto de um livro de RPG: '%s'. " +
            "Extraia de 5 a 10 termos chave que seriam úteis em um glossário. " +
            "Retorne a lista no formato 'Termo: Definição concisa.', com um termo por linha.",
            book.getDescription()
        );
    }
}
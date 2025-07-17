package mythosforge.chronicle_architect.prompt;

import mythosengine.spi.content.ContentGenerationContext;
import mythosengine.spi.prompt.PromptBuilder;
import mythosforge.chronicle_architect.models.Book;
import org.springframework.stereotype.Component;

@Component
public class ChapterStructurePromptBuilder implements PromptBuilder {

    @Override
    public boolean supports(ContentGenerationContext context) {
        return "STRUCTURE_CHAPTERS".equals(context.getGenerationType());
    }

    @Override
    public String build(ContentGenerationContext context) {
        Book book = (Book) context.getParameters().get("book");
        if (book == null) {
            throw new IllegalStateException("Contexto inválido para estruturar capítulos.");
        }
        return String.format(
            "Aja como um editor de livros de RPG experiente. Para um livro com o título '%s' e a seguinte descrição: '%s', sugira uma lista estruturada de títulos de capítulos. A lista deve ser apenas os títulos, um por linha.",
            book.getTitle(), book.getDescription()
        );
    }
}
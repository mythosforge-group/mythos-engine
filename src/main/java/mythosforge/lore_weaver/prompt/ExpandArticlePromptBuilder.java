package mythosforge.lore_weaver.prompt;

import mythosengine.spi.content.ContentGenerationContext;
import mythosengine.spi.prompt.PromptBuilder;
import mythosforge.lore_weaver.models.LoreArticle;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ExpandArticlePromptBuilder implements PromptBuilder {

    @Override
    public boolean supports(ContentGenerationContext context) {
        return "EXPAND_ARTICLE".equals(context.getGenerationType());
    }

    @Override
    public String build(ContentGenerationContext context) {
        if (!context.getParameters().containsKey("article")) {
             throw new IllegalStateException("Contexto inválido para expandir artigo de lore.");
        }
        LoreArticle article = (LoreArticle) context.getParameters().get("article");

        return String.format("""
            Aja como um autor de fantasia experiente. Pegue no seguinte rascunho de um artigo de lore sobre "%s" e expanda-o para um texto mais rico e detalhado, adicionando parágrafos, descrições sensoriais e contexto histórico.

            Rascunho Original:
            ---
            %s
            ---

            Versão Expandida:
            """,
            article.getNome(),
            article.getHistoria()
        );
    }
}
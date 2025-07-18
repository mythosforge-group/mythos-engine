package mythosforge.lore_weaver.prompt;

import mythosengine.spi.content.ContentGenerationContext;
import mythosengine.spi.prompt.PromptBuilder;
import mythosforge.lore_weaver.models.LoreArticle;
import org.springframework.stereotype.Component;

@Component
public class LoreConnectionPromptBuilder implements PromptBuilder {
    @Override
    public boolean supports(ContentGenerationContext context) {
        return "SUGGEST_CONNECTIONS".equals(context.getGenerationType());
    }

    @Override
    public String build(ContentGenerationContext context) {
        LoreArticle sourceArticle = (LoreArticle) context.getParameters().get("sourceArticle");
        if (sourceArticle == null) {
            throw new IllegalStateException("Contexto inválido para sugerir conexões de lore.");
        }

        return String.format("""
        Aja como um mestre de worldbuilding criativo. Com base no artigo de lore sobre "%s", cujo conteúdo é:
        ---
        %s
        ---
        
        Sugira entre 3 a 5 novas entidades (personagens, locais, etc.) relacionadas a este artigo.
        A resposta DEVE ser um array JSON válido. Siga estritamente o formato abaixo:
        [
          {
            "nome": "Nome Sugerido 1",
            "tipo": "TIPO_DA_ENTIDADE",
            "resumo": "Breve descrição da entidade e sua conexão.",
            "tipo_relacao": "TIPO_DA_RELAÇÃO"
          }
        ]
        """,
                sourceArticle.getNome(),
                sourceArticle.getHistoria()
        );
    }
}
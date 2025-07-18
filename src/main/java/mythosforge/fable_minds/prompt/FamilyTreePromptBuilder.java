package mythosforge.fable_minds.prompt;

import mythosengine.spi.content.ContentGenerationContext;
import mythosengine.spi.prompt.PromptBuilder;
import mythosforge.fable_minds.models.CharacterDnd;
import org.springframework.stereotype.Component;

@Component
public class FamilyTreePromptBuilder implements PromptBuilder {

    @Override
    public boolean supports(ContentGenerationContext context) {
        return "GERAR_LINHAGEM".equals(context.getGenerationType());
    }

    @Override
    public String build(ContentGenerationContext context) {
        CharacterDnd personagem = (CharacterDnd) context.getParameters().get("character");
        if (personagem == null) {
            throw new IllegalStateException("Contexto inválido para gerar linhagem.");
        }

        return String.format("""
            Gere uma árvore genealógica para o personagem de RPG abaixo com pelo menos 3 gerações: avós, pais e o personagem.

            Nome: %s
            Raça: %s
            Classe: %s
            História: %s

            A resposta deve ser no seguinte formato JSON, com os dados aninhados:
            {
              "nome": "%s",
              "ocupacao": "...",
              "origem": "...",
              "pais": [
                {
                  "nome": "...",
                  "ocupacao": "...",
                  "origem": "...",
                  "pais": [...]
                }
              ]
            }

            A saída DEVE ser estritamente JSON válido, sem explicações nem comentários.
            """,
                personagem.getNome(),
                personagem.getRaca().getName(),
                personagem.getCharacterClass().getName(),
                personagem.getHistoria(),
                personagem.getNome()
        );
    }
}
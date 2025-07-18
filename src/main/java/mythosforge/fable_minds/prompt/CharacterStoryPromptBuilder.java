package mythosforge.fable_minds.prompt;

import mythosengine.spi.content.ContentGenerationContext;
import mythosengine.spi.prompt.PromptBuilder;
import mythosforge.fable_minds.models.Campaign;
import mythosforge.fable_minds.models.CharacterClass;
import mythosforge.fable_minds.models.Race;
import org.springframework.stereotype.Component;

@Component
public class CharacterStoryPromptBuilder implements PromptBuilder {

    @Override
    public boolean supports(ContentGenerationContext context) {
        return "HISTORIA_PERSONAGEM".equals(context.getGenerationType());
    }

    @Override
    public String build(ContentGenerationContext context) {
        Campaign campaign = (Campaign) context.getParameters().get("campaign");
        Race race = (Race) context.getParameters().get("race");
        CharacterClass characterClass = (CharacterClass) context.getParameters().get("class");

        if (campaign == null || race == null || characterClass == null) {
            throw new IllegalStateException("Contexto inválido para gerar história de personagem.");
        }

        StringBuilder prompt = new StringBuilder();
        prompt.append("Gere uma história de personagem para o sistema de RPG '").append(campaign.getSystem().getName()).append("'.\n\n");
        prompt.append("Contexto do Universo:\n");
        prompt.append("- Campanha: \"").append(campaign.getTitle()).append("\", que possui o seguinte enredo: ").append(campaign.getDescription()).append(".\n\n");
        prompt.append("Detalhes do Personagem:\n");
        prompt.append("- Raça: ").append(race.getName()).append(".\n");
        prompt.append("- Classe: ").append(characterClass.getName()).append(".\n\n");
        prompt.append("Instruções:\n");
        prompt.append("Crie um background criativo e coeso. A história deve incluir:\n");
        prompt.append("1. Um nome para o personagem.\n");
        prompt.append("2. Sua origem e motivação.\n");
        prompt.append("3. Um evento marcante que o levou à vida de aventuras.\n");
        prompt.append("A resposta deve começar com a linha \"Nome: <nome do personagem>\" e seguir com a história em texto corrido.");

        return prompt.toString();
    }
}
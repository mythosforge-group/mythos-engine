package mythosforge.fable_minds.llm;

import mythosforge.fable_minds.config.security.auhentication.dto.CharacterClassDTO;
import mythosforge.fable_minds.config.security.auhentication.dto.RaceDTO;
import mythosforge.fable_minds.config.security.auhentication.dto.SystemDTO;
import mythosforge.fable_minds.models.Campaign;

public class PromptBuilder {

    public static String buildBasicPrompt(
            SystemDTO sistema,
            Campaign campanha,
            String raceName,
            String className
    ) {
        return String.format("""
            Gere a história de um personagem para um RPG.
            O personagem pertence ao sistema: %s.
            Ele faz parte da campanha chamada "%s", que possui o seguinte enredo: "%s".
            O personagem é da raça: %s, e da classe: %s.
            Gere um background criativo, incluindo nome, motivações, traumas, relações e possíveis objetivos futuros.
            Apresente em um texto bem estruturado, como se fosse o histórico da ficha de personagem.
            """,
            sistema.getName(),
            campanha.getTitle(),
            campanha.getDescription(),
            raceName,
            className
        );
    }

    public static String buildNamedPrompt(
            SystemDTO sistema,
            Campaign campanha,
            String raceName,
            String className
    ) {
        return String.format("""
            Gere a história de um personagem para um RPG.
            O personagem pertence ao sistema: %s.
            Ele faz parte da campanha chamada "%s", que possui o seguinte enredo: "%s".
            O personagem é da raça: %s, e da classe: %s.
            Gere um background criativo, incluindo nome, motivações, traumas, relações e possíveis objetivos futuros.
            Apresente em um texto bem estruturado, como se fosse o histórico da ficha de personagem.
            Comece com: Nome: <nome do personagem>
            As classes,raça, sistema tem que ser exatamente as que foram definidas.
            """,
            sistema.getName(),
            campanha.getTitle(),
            campanha.getDescription(),
            raceName,
            className
        );
    }
}
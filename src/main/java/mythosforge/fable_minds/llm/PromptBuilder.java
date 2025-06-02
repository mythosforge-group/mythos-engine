package mythosforge.fable_minds.llm;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import mythosforge.fable_minds.config.security.auhentication.dto.CharacterClassDTO;
import mythosforge.fable_minds.config.security.auhentication.dto.RaceDTO;
import mythosforge.fable_minds.config.security.auhentication.dto.SystemDTO;
import mythosforge.fable_minds.models.Campaign;
import mythosforge.fable_minds.models.NpcMessage;

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

    public static String buildFamilyTreePrompt(String nome, String historia, String raca, String classe) {
        return String.format("""
        Gere uma árvore genealógica para o personagem de RPG abaixo com pelo menos 3 gerações: avós, pais e o personagem.

        Nome: %s
        Raça: %s
        Classe: %s
        História: %s

        A resposta deve ser no seguinte formato JSON, com os dados aninhados conforme a árvore genealógica (nome, ocupação, origem e eventos marcantes):

        {
          "nome": "%s",
          "ocupacao": "...",
          "origem": "...",
          "eventos": ["..."],
          "pais": [
            {
              "nome": "...",
              "ocupacao": "...",
              "origem": "...",
              "eventos": ["..."],
              "pais": [
                {
                  "nome": "...",
                  "ocupacao": "...",
                  "origem": "...",
                  "eventos": ["..."]
                },
                {
                  "nome": "...",
                  "ocupacao": "...",
                  "origem": "...",
                  "eventos": ["..."]
                }
              ]
            },
            {
              "nome": "...",
              "ocupacao": "...",
              "origem": "...",
              "eventos": ["..."],
              "pais": [
                {
                  "nome": "...",
                  "ocupacao": "...",
                  "origem": "...",
                  "eventos": ["..."]
                },
                {
                  "nome": "...",
                  "ocupacao": "...",
                  "origem": "...",
                  "eventos": ["..."]
                }
              ]
            }
          ]
        }

        Utilize nomes criativos, coerentes com o universo de RPG. A saída **deve ser estritamente JSON válido**, sem explicações nem comentários.
        """,
                nome,
                raca,
                classe,
                historia,
                nome
        );
    }

    public static String buildSystemPrompt(String npcName, String descricao) {
        return String.format(
            "Interprete um personagem de RPG chamado %s.\n" +
            "Descrição do personagem: %s.\n\n" +
            "Você deve responder **apenas** com a fala do NPC, sem nenhum prefixo (ex: não escreva “%s:” na frente) nem repetir o histórico.\n" +
            "Responda de forma breve, natural e coerente com seu personagem. " +
            "Nunca peça desculpas, nunca recuse a conversa, e não repita saudações.",
            npcName,
            descricao,
            npcName
        );
    }

   public static String buildUserPrompt(String npcName, List<NpcMessage> history, String userContent) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Histórico da conversa:\n");

        List<NpcMessage> lastMessages = history.stream()
            .sorted(Comparator.comparing(NpcMessage::getTimestamp).reversed())
            .limit(9)
            .sorted(Comparator.comparing(NpcMessage::getTimestamp))
            .collect(Collectors.toList());

        for (NpcMessage msg : lastMessages) {
            String autor = msg.getRole().equals("user") ? "Jogador" : npcName;
            prompt.append(String.format("%s: %s\n", autor, msg.getContent()));
        }

        prompt.append("Jogador: ").append(userContent).append("\n");
        prompt.append(npcName).append(":");
        return prompt.toString();
    }
}
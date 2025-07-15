package mythosforge.fable_minds.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NpcConversationRequestDTO {
    private String npcName;
    private String descricao;
}
package mythosforge.fable_minds.config.security.auhentication.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NpcConversationResponseDTO {
    private Long id;
    private String npcName;
    private String descricao;
    private LocalDateTime createdAt;
    private List<NpcMessageResponseDTO> messages;
}
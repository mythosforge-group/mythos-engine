package mythosforge.fable_minds.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CampaignResponseDTO {
    private Long id;
    private String title;
    private String description;
    private Long user_id;
    private Long system_id;
}

package mythosforge.fable_minds.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CharacterClassDTO {
    private Long id;
    private String name;
    private String description;
    private Long systemId;
}
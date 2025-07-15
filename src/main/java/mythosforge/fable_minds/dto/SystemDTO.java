package mythosforge.fable_minds.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SystemDTO {
    private Long id;
    private String name;
    private String description;
}
package mythosforge.fable_minds.config.security.auhentication.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SystemDTO {
    private Long id;
    private String name;
    private String description;
}
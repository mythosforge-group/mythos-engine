package mythosforge.fable_minds.config.security.auhentication.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter 
@NoArgsConstructor
@AllArgsConstructor
public class NpcMessageResponseDTO {
    private Long id;
    private String role;
    private String content;
    private LocalDateTime timestamp;
}
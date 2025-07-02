package mythosengine.core.modules.content;

import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ContentGenerationContext {
    private String generationType; // Ex: "HISTORIA_PERSONAGEM", "MISSAO_SECUNDARIA", "LORE_CULTURA"
    private Map<String, Object> parameters; // Mapa flexível para passar qualquer dado (Campaign, Race, tema, etc.)
}
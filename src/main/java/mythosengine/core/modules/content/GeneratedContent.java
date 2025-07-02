package mythosengine.core.modules.content;

import java.util.List;
import java.util.Map;

import lombok.Builder;
import lombok.Getter;
import mythosengine.core.entity.Entity;

@Getter
@Builder
public class GeneratedContent {
    private String mainText; // O texto principal (história, descrição da missão, etc.)
    private List<Entity> createdEntities; // Entidades criadas ou populadas pelo módulo
     private Map<String, Object> metadata; // Metadados adicionais (ex: tags, categorias, etc.)
}
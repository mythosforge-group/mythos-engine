package mythosengine.core.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Getter
@ToString(of = {"id", "archetype"})
public class Entity {

    private final UUID id;
    private final String archetype;
    private final Map<String, Property> properties;
    private final List<Relationship> relationships;

    /**
     * Construtor para criar NOVAS entidades. Gera um novo UUID.
     * @param archetype O tipo da entidade (ex: "Personagem", "Item").
     */
    public Entity(String archetype) {
        this.id = UUID.randomUUID();
        this.archetype = archetype;
        this.properties = new java.util.HashMap<>();
        this.relationships = new java.util.ArrayList<>();
        System.out.println("KERNEL: Nova entidade criada (ID: " + this.id + ", Arquétipo: " + this.archetype + ")");
    }

    /**
     * Construtor para DESSERIALIZAÇÃO (usado pelo Jackson).
     * Recria uma entidade a partir de dados existentes (ex: de um ficheiro JSON).
     * @param id O UUID existente da entidade.
     * @param archetype O arquétipo existente.
     * @param properties O mapa de propriedades existente.
     * @param relationships A lista de relações existente.
     */
    @JsonCreator // Diz ao Jackson para usar este construtor
    public Entity(
            @JsonProperty("id") UUID id,
            @JsonProperty("archetype") String archetype,
            @JsonProperty("properties") Map<String, Property> properties,
            @JsonProperty("relationships") List<Relationship> relationships
    ) {
        this.id = id;
        this.archetype = archetype;
        this.properties = properties;
        this.relationships = relationships;
        System.out.println("KERNEL: Entidade RECRIADA a partir de dados (ID: " + this.id + ")");
    }


    public void addProperty(String name, Object value) {
        System.out.println("KERNEL: Propriedade '" + name + "' adicionada/atualizada na entidade " + this.id);
        this.properties.put(name, new Property(value, value.getClass()));
    }

    public Optional<Object> getProperty(String name) {
        return Optional.ofNullable(properties.get(name)).map(Property::value);
    }

    public void addRelationship(UUID targetId, String relationshipType) {
        Relationship rel = new Relationship(this.id, targetId, relationshipType);
        this.relationships.add(rel);
        System.out.println("KERNEL: Relação '" + relationshipType + "' adicionada de " + this.id + " para " + targetId);
    }
}

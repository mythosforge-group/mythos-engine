package mythosengine.core.entity;

import java.util.UUID;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(of = {"id", "archetype"})
public class Entity {
    private final UUID id;
    private final String archetype;
    private final Map<String, Property> properties;
    private final List<Relationship> relationships;

    public Entity(String archetype) {
        this.id = UUID.randomUUID();
        this.archetype = archetype;
        this.properties = new HashMap<>();
        this.relationships = new ArrayList<>();
        System.out.println("KERNEL: Entidade criada (ID: " + this.id + ", Arquétipo: " + this.archetype + ")");
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
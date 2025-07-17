package mythosengine.core.persistence;

import mythosengine.core.entity.Entity;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface PersistencePort {
    void save(Entity entity);
    Optional<Entity> findById(UUID entityId);
    Collection<Entity> findAll();
    void deleteById(UUID entityId);
}

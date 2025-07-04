package mythosengine.services.storage;

// services/storage/InMemoryStorageAdapter.java
import mythosengine.core.entity.Entity;
import mythosengine.core.persistence.PersistencePort;
import org.springframework.stereotype.Repository;
import org.springframework.context.annotation.Primary;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Primary
public class InMemoryStorageAdapter implements PersistencePort {
    private final Map<UUID, Entity> database = new ConcurrentHashMap<>();

    @Override
    public void save(Entity entity) {
        System.out.println("STORAGE(InMemory): Salvando entidade " + entity.getId());
        database.put(entity.getId(), entity);
    }

    @Override
    public Optional<Entity> findById(UUID entityId) {
        System.out.println("STORAGE(InMemory): Buscando entidade " + entityId);
        return Optional.ofNullable(database.get(entityId));
    }

    public Collection<Entity> findAll() {
        System.out.println("STORAGE(InMemory): Retornando snapshot de todas as " + database.size() + " entidades.");
        return database.values();
    }
}
package mythosengine.services.storage;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import mythosengine.core.entity.Entity;
import mythosengine.core.persistence.PersistencePort;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Primary
@Repository // Marcamos como um componente Spring
public class JsonFileStorageAdapter implements PersistencePort {

    private final Path storageDirectory = Paths.get("mythos_engine_data");
    private final ObjectMapper objectMapper;

    public JsonFileStorageAdapter() {

        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(storageDirectory);
            System.out.println("FRAMEWORK_STORAGE(JSON): Diretório de armazenamento inicializado em: " + storageDirectory.toAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível criar o diretório de armazenamento para o Mythos Engine.", e);
        }
    }

    @Override
    public void save(Entity entity) {
        try {
            Path filePath = storageDirectory.resolve(entity.getId().toString() + ".json");
            String jsonContent = objectMapper.writeValueAsString(entity);
            Files.writeString(filePath, jsonContent);
            System.out.println("STORAGE(JSON): Entidade " + entity.getId() + " salva em " + filePath);
        } catch (IOException e) {
            throw new RuntimeException("Falha ao salvar a entidade " + entity.getId(), e);
        }
    }

    @Override
    public Optional<Entity> findById(UUID entityId) {
        try {
            Path filePath = storageDirectory.resolve(entityId.toString() + ".json");
            if (Files.exists(filePath)) {
                String jsonContent = Files.readString(filePath);
                Entity entity = objectMapper.readValue(jsonContent, Entity.class);
                System.out.println("STORAGE(JSON): Entidade " + entityId + " carregada do ficheiro.");
                return Optional.of(entity);
            }
        } catch (IOException e) {
            System.err.println("Falha ao carregar a entidade " + entityId + ": " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public void deleteById(UUID entityId) {
        try {
            Path filePath = storageDirectory.resolve(entityId.toString() + ".json");
            Files.deleteIfExists(filePath);
            System.out.println("STORAGE(JSON): Entidade " + entityId + " deletada.");
        } catch (IOException e) {
            System.err.println("Falha ao deletar a entidade " + entityId + ": " + e.getMessage());
        }
    }


    public Collection<Entity> findAll() {
        try (Stream<Path> stream = Files.walk(this.storageDirectory, 1)) {
            return stream
                    .filter(file -> !Files.isDirectory(file))
                    .map(path -> {
                        try {
                            return findById(UUID.fromString(path.getFileName().toString().replace(".json", ""))).orElse(null);
                        } catch (Exception e) {
                            return null;
                        }
                    })
                    .filter(java.util.Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Falha ao listar todas as entidades.", e);
        }
    }
}

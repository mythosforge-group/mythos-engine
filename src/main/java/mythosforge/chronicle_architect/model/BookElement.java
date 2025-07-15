package mythosforge.chronicle_architect.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

/**
 * Classe base abstrata para todas as entidades principais do Chronicle Architect.
 * Contém o ID da aplicação e a "ponte" para a entidade correspondente no framework Mythos Engine.
 */
@MappedSuperclass
@Data
public abstract class BookElement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Armazena o ID da entidade correspondente no Mythos Engine.
     * Este é o vínculo essencial entre o modelo da aplicação e o framework.
     */
    @Column(name = "framework_entity_id", unique = true, nullable = false, updatable = false)
    private UUID entityId;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;
}
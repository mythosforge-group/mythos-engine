package mythosforge.chronicle_architect.models;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

/**
 * Classe base para todos os componentes de regras (Habilidades, Magias, Itens, etc).
 * Vinculada a um livro e ao framework Mythos Engine.
 */
@Entity
@Table(name = "rule_component")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "component_type", discriminatorType = DiscriminatorType.STRING)
@Data
public abstract class RuleComponent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "framework_entity_id", unique = true, nullable = false, updatable = false)
    private UUID entityId;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;
}
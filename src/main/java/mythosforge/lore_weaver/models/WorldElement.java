package mythosforge.lore_weaver.models;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;


@Entity
@Table(name = "world_element") // Uma nova tabela para os elementos de lore
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "element_type", discriminatorType = DiscriminatorType.STRING)
@Data
public abstract class WorldElement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "framework_entity_id", unique = true, nullable = false, updatable = false)
    private UUID entityId;


    @Column(nullable = false)
    private String nome;


    @Column(columnDefinition = "TEXT")
    private String historia;


}

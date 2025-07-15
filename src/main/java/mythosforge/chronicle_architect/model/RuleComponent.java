package mythosforge.chronicle_architect.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Classe base para componentes de regras reutilizáveis (Habilidades, Magias, Itens).
 * Usa uma estratégia de herança para permitir especializações.
 */
@Entity
@Table(name = "rule_component")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "component_type", discriminatorType = DiscriminatorType.STRING)
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class RuleComponent extends BookElement {

    @Column(name = "tags")
    private String tags; // Ex: "Fogo, Dano em Área, Divino"
}
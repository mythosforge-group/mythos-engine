package mythosforge.chronicle_architect.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@DiscriminatorValue("SKILL")
@Data
@EqualsAndHashCode(callSuper = true)
public class SkillComponent extends RuleComponent {
    private String cost; // Ex: "10 PM" ou "Ação Padrão"
    private String effect; // O que a habilidade faz
}
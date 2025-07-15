package mythosforge.chronicle_architect.models;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@DiscriminatorValue("SKILL")
@Data
@EqualsAndHashCode(callSuper = true)
public class SkillComponent extends RuleComponent {
    private String cost; // Ex: "10 PM", "Ação Padrão"
    private String effectSummary; // Ex: "Causa 2d6 de dano de fogo."
}
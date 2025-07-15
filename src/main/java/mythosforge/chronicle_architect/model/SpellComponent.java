package mythosforge.chronicle_architect.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@DiscriminatorValue("SPELL")
@Data
@EqualsAndHashCode(callSuper = true)
public class SpellComponent extends RuleComponent {
    private int level;
    private String school; // Ex: "Evocação", "Ilusão"
    private String castingTime;
    private String range;
    private String duration;
}
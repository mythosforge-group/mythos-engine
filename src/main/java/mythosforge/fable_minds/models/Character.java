package mythosforge.fable_minds.models;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "character")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "sistema", discriminatorType = DiscriminatorType.STRING)
@Data
public abstract class Character {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String historia;
    private Integer nivel;
    private Integer xp;

    @ManyToOne
    @JoinColumn(name = "campaign_id", nullable = false)
    @JsonBackReference("campaign-characters")
    private Campaign campanha;

    @ManyToOne
    @JoinColumn(name = "race_id", nullable = false)
    @JsonBackReference("race-characters")
    private Race raca;

    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    @JsonBackReference("class-characters")
    private CharacterClass characterClass;
}
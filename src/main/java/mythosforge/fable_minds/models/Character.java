package mythosforge.fable_minds.models;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
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
    private Integer nivel;

    private Integer xp;

    @Column(columnDefinition = "TEXT")
    private String historia;

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
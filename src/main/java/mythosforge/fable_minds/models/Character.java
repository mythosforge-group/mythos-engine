package mythosforge.fable_minds.models;

import jakarta.persistence.*;

@Entity
public class Character {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String background;

    @ManyToOne
    @JoinColumn(name = "campaign_id")
    private Campaign campaign;
}


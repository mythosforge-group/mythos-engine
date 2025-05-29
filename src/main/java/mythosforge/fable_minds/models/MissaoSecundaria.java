package mythosforge.fable_minds.models;

import java.util.List;

import org.hibernate.annotations.Comment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "missao_secundaria")
@Data
public class MissaoSecundaria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("The title of the secondary mission")
    @Column(nullable = false, length = 100)
    private String titulo;

    @Comment("The history of the secondary mission")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String historia;

    @Comment("The characteres involved in the secondary mission")
    @ManyToMany
    @JoinTable(
        name = "missao_secundaria_personagens",
        joinColumns = @JoinColumn(name = "missao_secundaria_id"),
        inverseJoinColumns = @JoinColumn(name = "personagens_id")
    )
    private List<Character> personagens;


    @ManyToOne
    @JoinColumn(nullable = false, name = "campaign_id")
    @Comment("The campaign to which this secondary mission belongs")
    private Campaign campanha;
}
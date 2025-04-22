package mythosforge.fable_minds.models;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.Comment;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Table(name = "campaigns")
@Data
public class Campaign {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @Comment("The title of the campaign")
    @Size(min = 1, max = 100, message = "Campaign title must be between 1 and 100 characters long")
    private String title;

    @ManyToOne
    @JoinColumn(nullable = false, name = "user_id")
    @JsonBackReference      // ← controla o “lado filho” da relação
    @Comment("The user who owns the campaign")
    private Users owner;

    @Column(length = 512)
    @Comment("The narrative description of the campaign")
    private String description;

    @OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL)
    @Comment("List of characters in the campaign, ")
    @JsonManagedReference               // controla o “lado pai” da relação
    private List<Character> characters = new ArrayList<>();
}
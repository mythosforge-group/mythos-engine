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
import lombok.Data;

@Entity
@Table(name = "class")
@Data
public class CharacterClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    @Comment("The name of the character class, e.g., Fighter, Wizard")
    private String name;

    @Column(length = 512)
    @Comment("A brief description of the character class")
    private String description;

    @ManyToOne
    @JoinColumn(name = "system_id", nullable = false)
    @JsonBackReference("system-classes")
    @Comment("The game system this class belongs to")
    private System system;

    @OneToMany(mappedBy = "characterClass", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("class-characters")
    @Comment("List of characters of this class")
    private List<Character> characters = new ArrayList<>();
}
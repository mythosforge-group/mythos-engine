package mythosforge.fable_minds.models;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.Comment;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "system")
@Data
public class System {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    @Comment("The name of the game system, e.g., D&D, Tormenta")
    private String name;

    @Column(length = 512)
    @Comment("A brief description of the game system")
    private String description;

    @OneToMany(mappedBy = "system", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("system-races")
    @Comment("List of races defined by this system")
    private List<Race> races = new ArrayList<>();

    @OneToMany(mappedBy = "system", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("system-classes")
    @Comment("List of character classes defined by this system")
    private List<CharacterClass> classes = new ArrayList<>();

    @OneToMany(mappedBy = "system", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("system-campaigns")
    @Comment("List of campaigns under this system")
    private List<Campaign> campaigns = new ArrayList<>();
}
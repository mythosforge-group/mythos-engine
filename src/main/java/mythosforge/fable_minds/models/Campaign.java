package mythosforge.fable_minds.models;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.Comment;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
import mythosengine.config.security.authentication.service.auth.models.Users;

@Entity
@Table(name = "campaign")
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
    @Comment("The user who owns the campaign")
    private Users user;

    @Column(length = 512)
    @Comment("The narrative description of the campaign")
    private String description;

    @ManyToOne
    @JoinColumn(nullable = false, name = "system_id")
    @Comment("The game system this campaign is based on, e.g., D&D, Tormenta")
    @JsonBackReference("system-campaigns")
    private System system;

    @OneToMany(mappedBy = "campanha", cascade = CascadeType.ALL, orphanRemoval = true)
    @Comment("List of characters in the campaign")
    @JsonIgnore 
    private List<Character> characters = new ArrayList<>();
}
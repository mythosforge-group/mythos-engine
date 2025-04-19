package mythosforge.fable_minds.models;

import org.hibernate.annotations.Comment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Table(name = "roles")
@Data
@Comment("The role of the user in the system, e.g., Admin, Player, etc.")
public class Role { 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    @NotBlank(message = "Role name cannot be blank")
    @Size(max = 50, message = "Role name must be less than 50 characters")
    private String name;

    // @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    // @Comment("Set of users associated with this role")
    // private Set<Users> users;
}
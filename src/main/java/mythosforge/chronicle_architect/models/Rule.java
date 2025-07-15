package mythosforge.chronicle_architect.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "rule")
@Data
public class Rule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
}
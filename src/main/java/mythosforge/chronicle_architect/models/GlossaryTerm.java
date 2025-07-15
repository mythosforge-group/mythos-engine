package mythosforge.chronicle_architect.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "glossary_term")
@Data
public class GlossaryTerm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String term;

    @Column(columnDefinition = "TEXT")
    private String definition;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
}
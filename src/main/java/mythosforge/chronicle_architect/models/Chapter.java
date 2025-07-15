package mythosforge.chronicle_architect.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "chapter")
@Data
public class Chapter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
}
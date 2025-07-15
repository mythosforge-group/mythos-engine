package mythosforge.chronicle_architect.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Representa um capítulo dentro de um Rulebook.
 */
@Entity
@Table(name = "book_chapter")
@Getter
@Setter
public class BookChapter extends BookElement {

    @Column(name = "chapter_number")
    private int chapterNumber;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rulebook_id", nullable = false)
    private Rulebook rulebook;
}
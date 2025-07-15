package mythosforge.chronicle_architect.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa um livro de regras ou um manual de lore.
 * É o container principal para capítulos e componentes de regras.
 */
@Entity
@Table(name = "rulebook")
@Getter
@Setter
public class Rulebook extends BookElement {

    @Column(nullable = false)
    private String version;

    /**
     * O campo 'author' agora é uma String simples, removendo a dependência
     * da entidade Users para simplificar.
     */
    @Column(name = "author")
    private String author;

    @OneToMany(mappedBy = "rulebook", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookChapter> chapters = new ArrayList<>();
}
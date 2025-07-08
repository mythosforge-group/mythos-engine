package mythosforge.lore_weaver.models;



import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "lore_article")
@DiscriminatorValue("LORE_ARTICLE")
@Getter
@Setter
public class LoreArticle extends WorldElement {

    @Column(name = "article_type", nullable = false)
    private String articleType;

    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;
}

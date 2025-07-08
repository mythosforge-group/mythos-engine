package mythosforge.lore_weaver.repository;



import mythosforge.lore_weaver.models.LoreArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoreArticleRepository extends JpaRepository<LoreArticle, Long> {

}
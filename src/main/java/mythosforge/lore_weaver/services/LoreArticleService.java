package mythosforge.lore_weaver.services;



import mythosforge.lore_weaver.models.LoreArticle;
import mythosforge.lore_weaver.repository.LoreArticleRepository;
import jakarta.persistence.EntityNotFoundException;
import mythosengine.core.entity.Entity;
import mythosengine.core.persistence.PersistencePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class LoreArticleService {

    private final LoreArticleRepository loreArticleRepository;
    private final PersistencePort persistencePort;

    public LoreArticleService(LoreArticleRepository loreArticleRepository, PersistencePort persistencePort) {
        this.loreArticleRepository = loreArticleRepository;
        this.persistencePort = persistencePort;
    }

    public LoreArticle create(LoreArticle article) {
        Entity frameworkEntity = new Entity(article.getArticleType());
        syncPropertiesToFramework(article, frameworkEntity);

        persistencePort.save(frameworkEntity);

        article.setEntityId(frameworkEntity.getId());

        return loreArticleRepository.save(article);
    }


    public LoreArticle expandHistory(Long id, String expandedContent) {

        LoreArticle article = findById(id);


        article.setHistoria(expandedContent);


        Entity frameworkEntity = persistencePort.findById(article.getEntityId())
                .orElseThrow(() -> new IllegalStateException("Entidade do framework não encontrada para o artigo: " + id));

        syncPropertiesToFramework(article, frameworkEntity);
        persistencePort.save(frameworkEntity);


        return loreArticleRepository.save(article);
    }


    public LoreArticle update(Long id, LoreArticle updatedArticleData) {
        LoreArticle existingArticle = findById(id);

        Entity frameworkEntity = persistencePort.findById(existingArticle.getEntityId())
                .orElseThrow(() -> new IllegalStateException("Entidade do framework não encontrada para o artigo: " + id));

        existingArticle.setNome(updatedArticleData.getNome());
        existingArticle.setHistoria(updatedArticleData.getHistoria());
        existingArticle.setArticleType(updatedArticleData.getArticleType());
        existingArticle.setSummary(updatedArticleData.getSummary());

        syncPropertiesToFramework(existingArticle, frameworkEntity);
        persistencePort.save(frameworkEntity);

        return loreArticleRepository.save(existingArticle);
    }

    public void delete(Long id) {
        LoreArticle articleToDelete = findById(id);
        UUID frameworkEntityId = articleToDelete.getEntityId();

        loreArticleRepository.deleteById(id);

        persistencePort.deleteById(frameworkEntityId);
    }

    @Transactional(readOnly = true)
    public LoreArticle findById(Long id) {
        return loreArticleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Artigo de Lore não encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public List<LoreArticle> findAll() {
        return loreArticleRepository.findAll();
    }

    private void syncPropertiesToFramework(LoreArticle article, Entity entity) {
        entity.addProperty("nome", article.getNome());
        entity.addProperty("articleType", article.getArticleType());
        entity.addProperty("summary", article.getSummary());
        entity.addProperty("historia", article.getHistoria());
    }
}


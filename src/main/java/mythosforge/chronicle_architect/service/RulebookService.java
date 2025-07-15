package mythosforge.chronicle_architect.service;

import jakarta.persistence.EntityNotFoundException;
import mythosengine.core.entity.Entity;
import mythosengine.core.persistence.PersistencePort;
import mythosforge.chronicle_architect.model.Rulebook;
import mythosforge.chronicle_architect.repository.RulebookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class RulebookService {

    private final RulebookRepository rulebookRepository;
    private final PersistencePort persistencePort;

    public RulebookService(RulebookRepository rulebookRepository, PersistencePort persistencePort) {
        this.rulebookRepository = rulebookRepository;
        this.persistencePort = persistencePort;
    }

    public Rulebook create(Rulebook rulebook) {
        Entity frameworkEntity = new Entity("Rulebook");
        syncPropertiesToFramework(rulebook, frameworkEntity);
        persistencePort.save(frameworkEntity);
        rulebook.setEntityId(frameworkEntity.getId());
        return rulebookRepository.save(rulebook);
    }

    public Rulebook update(Long id, Rulebook rulebookDetails) {
        Rulebook existing = findById(id);
        Entity frameworkEntity = persistencePort.findById(existing.getEntityId())
                .orElseThrow(() -> new IllegalStateException("Entidade do framework não encontrada para o Rulebook: " + id));

        existing.setName(rulebookDetails.getName());
        existing.setDescription(rulebookDetails.getDescription());
        existing.setVersion(rulebookDetails.getVersion());
        
        // Atualiza o autor
        existing.setAuthor(rulebookDetails.getAuthor());

        syncPropertiesToFramework(existing, frameworkEntity);
        persistencePort.save(frameworkEntity);

        return rulebookRepository.save(existing);
    }

    public void delete(Long id) {
        Rulebook toDelete = findById(id);
        UUID frameworkEntityId = toDelete.getEntityId();
        rulebookRepository.deleteById(id);
        persistencePort.deleteById(frameworkEntityId);
    }

    @Transactional(readOnly = true)
    public Rulebook findById(Long id) {
        return rulebookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rulebook não encontrado com ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<Rulebook> findAll() {
        return rulebookRepository.findAll();
    }

    private void syncPropertiesToFramework(Rulebook rulebook, Entity entity) {
        entity.addProperty("name", rulebook.getName());
        entity.addProperty("version", rulebook.getVersion());
        if (rulebook.getAuthor() != null && !rulebook.getAuthor().isBlank()) {
            // Alteração principal aqui: lê o campo 'author' diretamente como String.
            entity.addProperty("author", rulebook.getAuthor());
        }
    }
}
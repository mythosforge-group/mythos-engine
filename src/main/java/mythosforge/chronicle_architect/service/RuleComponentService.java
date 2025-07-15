package mythosforge.chronicle_architect.service;

import jakarta.persistence.EntityNotFoundException;
import mythosengine.core.entity.Entity;
import mythosengine.core.persistence.PersistencePort;
import mythosforge.chronicle_architect.models.RuleComponent;
import mythosforge.chronicle_architect.repository.RuleComponentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class RuleComponentService {

    private final RuleComponentRepository repository;
    private final PersistencePort persistencePort;

    public RuleComponentService(RuleComponentRepository repository, PersistencePort persistencePort) {
        this.repository = repository;
        this.persistencePort = persistencePort;
    }

    public <T extends RuleComponent> T create(T component) {
        Entity frameworkEntity = new Entity(component.getClass().getSimpleName());
        syncPropertiesToFramework(component, frameworkEntity);
        persistencePort.save(frameworkEntity);
        component.setEntityId(frameworkEntity.getId());
        return repository.save(component);
    }

    public RuleComponent findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Componente de Regra não encontrado: " + id));
    }

    public List<RuleComponent> findAll() {
        return repository.findAll();
    }

    // Método crucial para a visualização de grafos
    public void addPrerequisite(Long skillId, Long prerequisiteId) {
        RuleComponent skill = findById(skillId);
        RuleComponent prerequisite = findById(prerequisiteId);

        Entity skillEntity = persistencePort.findById(skill.getEntityId())
                .orElseThrow(() -> new IllegalStateException("Entidade do framework não encontrada para a Habilidade: " + skillId));

        skillEntity.addRelationship(prerequisite.getEntityId(), "HAS_PREREQUISITE");
        persistencePort.save(skillEntity);
    }

    private void syncPropertiesToFramework(RuleComponent component, Entity entity) {
        if (component.getName() != null) entity.addProperty("name", component.getName());
        if (component.getDescription() != null) entity.addProperty("description", component.getDescription());
    }
}
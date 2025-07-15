package mythosforge.chronicle_architect.service;

import jakarta.persistence.EntityNotFoundException;
import mythosengine.core.entity.Entity;
import mythosengine.core.persistence.PersistencePort;
import mythosforge.chronicle_architect.model.RuleComponent;
import mythosforge.chronicle_architect.repository.RuleComponentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RuleComponentService {

    private final RuleComponentRepository ruleComponentRepository;
    private final PersistencePort persistencePort;

    public RuleComponentService(RuleComponentRepository ruleComponentRepository, PersistencePort persistencePort) {
        this.ruleComponentRepository = ruleComponentRepository;
        this.persistencePort = persistencePort;
    }

    public <T extends RuleComponent> T create(T component) {
        Entity frameworkEntity = new Entity(component.getClass().getSimpleName());
        syncPropertiesToFramework(component, frameworkEntity);
        persistencePort.save(frameworkEntity);
        component.setEntityId(frameworkEntity.getId());
        return ruleComponentRepository.save(component);
    }

    public RuleComponent findById(Long id) {
        return ruleComponentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Componente de regra não encontrado: " + id));
    }

    public void addPrerequisite(Long componentId, Long prerequisiteId) {
        RuleComponent component = findById(componentId);
        RuleComponent prerequisite = findById(prerequisiteId);

        Entity componentEntity = persistencePort.findById(component.getEntityId())
                .orElseThrow(() -> new IllegalStateException("Entidade do framework não encontrada para o Componente: " + componentId));

        componentEntity.addRelationship(prerequisite.getEntityId(), "HAS_PREREQUISITE");
        persistencePort.save(componentEntity);
    }

    private void syncPropertiesToFramework(RuleComponent component, Entity entity) {
        entity.addProperty("name", component.getName());
        entity.addProperty("description", component.getDescription());
        entity.addProperty("tags", component.getTags());
    }
}
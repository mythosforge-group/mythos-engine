package mythosforge.fable_minds.service;

import java.util.List;
import java.util.UUID;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mythosengine.core.entity.Entity;
import mythosengine.core.persistence.PersistencePort;
import mythosforge.fable_minds.models.CharacterDnd;
import mythosforge.fable_minds.repository.CharacterDndRepository;
import mythosforge.fable_minds.service.interfaces.ICharacterDndService;

@Service
@Transactional
public class CharacterDndService implements ICharacterDndService {

    private final CharacterDndRepository characterDndRepository;
    private final PersistencePort persistencePort;

    public CharacterDndService(CharacterDndRepository characterDndRepository, PersistencePort persistencePort) {
        this.characterDndRepository = characterDndRepository;
        this.persistencePort = persistencePort;
    }

    @Override
    public CharacterDnd create(CharacterDnd character) {
        // 1. Criar a Entidade do framework correspondente
        Entity frameworkEntity = new Entity("CharacterDnd");
        syncPropertiesToFramework(character, frameworkEntity); // Sincroniza os dados

        // 2. Salvar a Entidade no sistema do framework
        persistencePort.save(frameworkEntity);

        // 3. Vincular o ID do framework ao personagem da aplicação
        character.setEntityId(frameworkEntity.getId());

        // 4. Salvar o personagem no banco de dados da aplicação
        return characterDndRepository.save(character);
    }

    @Override
    public CharacterDnd update(Long id, CharacterDnd updatedCharacterData) {
        // 1. Encontrar o personagem existente no banco da aplicação
        CharacterDnd existingCharacter = findById(id);

        // 2. Carregar a entidade correspondente do framework
        Entity frameworkEntity = persistencePort.findById(existingCharacter.getEntityId())
                .orElseThrow(() -> new IllegalStateException("Entidade do framework não encontrada para o personagem: " + id));

        // 3. Atualizar os dados do personagem da aplicação
        existingCharacter.setNome(updatedCharacterData.getNome());
        existingCharacter.setHistoria(updatedCharacterData.getHistoria());
        existingCharacter.setNivel(updatedCharacterData.getNivel());
        // ... (copiar todos os outros campos)
        existingCharacter.setCarisma(updatedCharacterData.getCarisma());

        // 4. Sincronizar as propriedades atualizadas para a entidade do framework
        syncPropertiesToFramework(existingCharacter, frameworkEntity);
        persistencePort.save(frameworkEntity);

        // 5. Salvar o personagem atualizado no banco da aplicação
        return characterDndRepository.save(existingCharacter);
    }

    @Override
    public void delete(Long id) {
        // 1. Encontrar o personagem para obter o ID da entidade do framework
        CharacterDnd characterToDelete = findById(id);
        UUID frameworkEntityId = characterToDelete.getEntityId();

        // 2. Deletar o personagem do banco da aplicação
        characterDndRepository.deleteById(id);

        // 3. Deletar a entidade correspondente do framework
        persistencePort.deleteById(frameworkEntityId);
    }

    // --- Métodos de Leitura (Não precisam de alteração) ---

    @Override
    @Transactional(readOnly = true)
    public CharacterDnd findById(Long id) {
        return characterDndRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Personagem DnD não encontrado: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CharacterDnd> findAll() {
        return characterDndRepository.findAll();
    }

    // --- Método Auxiliar Privado para evitar duplicação de código ---

    /**
     * Sincroniza as propriedades de um objeto CharacterDnd para uma Entidade do framework.
     * @param character O objeto de origem da aplicação.
     * @param entity O objeto de destino do framework.
     */
    private void syncPropertiesToFramework(CharacterDnd character, Entity entity) {
        entity.addProperty("nome", character.getNome());
        entity.addProperty("nivel", character.getNivel());
        if (character.getRaca() != null) {
            entity.addProperty("raca", character.getRaca().getName());
        }
        if (character.getCharacterClass() != null) {
            entity.addProperty("classe", character.getCharacterClass().getName());
        }
    }
}
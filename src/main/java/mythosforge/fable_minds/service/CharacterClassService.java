package mythosforge.fable_minds.service;

import java.util.List;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mythosforge.fable_minds.models.CharacterClass;
import mythosforge.fable_minds.repository.CharacterClassRepository;
import mythosforge.fable_minds.service.CharacterClassService;
import mythosforge.fable_minds.service.interfaces.ICharacterClassService;

@Service
@Transactional
public class CharacterClassService implements ICharacterClassService {

    private final CharacterClassRepository characterClassRepository;

    public CharacterClassService(CharacterClassRepository characterClassRepository) {
        this.characterClassRepository = characterClassRepository;
    }

    @Override
    public CharacterClass create(CharacterClass characterClass) {
        return characterClassRepository.save(characterClass);
    }

    @Override
    public CharacterClass update(Long id, CharacterClass characterClass) {
        CharacterClass existing = characterClassRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("CharacterClass not found: " + id));
        existing.setName(characterClass.getName());
        existing.setDescription(characterClass.getDescription());
        existing.setSystem(characterClass.getSystem());
        return characterClassRepository.save(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public CharacterClass findById(Long id) {
        return characterClassRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("CharacterClass not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CharacterClass> findAll() {
        return characterClassRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CharacterClass> findBySystemId(Long systemId) {
        return characterClassRepository.findBySystemId(systemId);
    }

    @Override
    public void delete(Long id) {
        if (!characterClassRepository.existsById(id)) {
            throw new EntityNotFoundException("CharacterClass not found: " + id);
        }
        characterClassRepository.deleteById(id);
    }
}
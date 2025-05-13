package mythosforge.fable_minds.service;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mythosforge.fable_minds.config.security.auhentication.dto.CharacterClassDTO;
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
    public List<CharacterClassDTO> findAllDto() {
        return characterClassRepository.findAll()
            .stream()
            .map(cc -> new CharacterClassDTO(
                cc.getId(), cc.getName(), cc.getDescription(), cc.getSystem().getId()
            ))
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CharacterClassDTO findByIdDto(Long id) {
        CharacterClass cc = characterClassRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("CharacterClass not found: " + id));
        return new CharacterClassDTO(
            cc.getId(), cc.getName(), cc.getDescription(), cc.getSystem().getId()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<CharacterClassDTO> findBySystemIdDto(Long systemId) {
        return characterClassRepository.findBySystemId(systemId)
            .stream()
            .map(cc -> new CharacterClassDTO(
                cc.getId(), cc.getName(), cc.getDescription(), cc.getSystem().getId()
            ))
            .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        if (!characterClassRepository.existsById(id)) {
            throw new EntityNotFoundException("CharacterClass not found: " + id);
        }
        characterClassRepository.deleteById(id);
    }
}
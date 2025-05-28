package mythosforge.fable_minds.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mythosforge.fable_minds.models.Character;
import mythosforge.fable_minds.repository.CharacterRepository;
import mythosforge.fable_minds.service.interfaces.ICharacterService;
import mythosforge.fable_minds.exceptions.BusinessException;

@Service
@Transactional
public class CharacterService implements ICharacterService {

    private final CharacterRepository characterRepository;

    public CharacterService(CharacterRepository characterRepository) {
        this.characterRepository = characterRepository;
    }

    @Override
    public Character create(Character character) {
        return characterRepository.save(character);
    }

    @Override
    public Character update(Long id, Character character) {
        Character existing = characterRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Personagem não encontrado: " + id));
        existing.setNome(character.getNome());
        existing.setHistoria(character.getHistoria());
        existing.setNivel(character.getNivel());
        existing.setXp(character.getXp());
        existing.setCampanha(character.getCampanha());
        existing.setRaca(character.getRaca());
        existing.setCharacterClass(character.getCharacterClass());
        return characterRepository.save(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public Character findById(Long id) {
        return characterRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Personagem não encontrado: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Character> findAll() {
        return characterRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        if (!characterRepository.existsById(id)) {
            throw new BusinessException("Personagem não encontrado: " + id);
        }
        characterRepository.deleteById(id);
    }
}

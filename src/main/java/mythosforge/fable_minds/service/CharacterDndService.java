package mythosforge.fable_minds.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mythosforge.fable_minds.exceptions.BusinessException;
import mythosforge.fable_minds.models.CharacterDnd;
import mythosforge.fable_minds.repository.CharacterDndRepository;
import mythosforge.fable_minds.service.interfaces.ICharacterDndService;

@Service
@Transactional
public class CharacterDndService implements ICharacterDndService {

    private final CharacterDndRepository characterDndRepository;

    public CharacterDndService(CharacterDndRepository characterDndRepository) {
        this.characterDndRepository = characterDndRepository;
    }

    @Override
    public CharacterDnd create(CharacterDnd character) {
        return characterDndRepository.save(character);
    }

    @Override
    public CharacterDnd update(Long id, CharacterDnd character) {
        CharacterDnd existing = characterDndRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Personagem DnD não encontrado: " + id));
        existing.setNome(character.getNome());
        existing.setHistoria(character.getHistoria());
        existing.setNivel(character.getNivel());
        existing.setXp(character.getXp());
        existing.setCampanha(character.getCampanha());
        existing.setRaca(character.getRaca());
        existing.setCharacterClass(character.getCharacterClass());
        existing.setForca(character.getForca());
        existing.setDestreza(character.getDestreza());
        existing.setConstituicao(character.getConstituicao());
        existing.setInteligencia(character.getInteligencia());
        existing.setSabedoria(character.getSabedoria());
        existing.setCarisma(character.getCarisma());
        return characterDndRepository.save(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public CharacterDnd findById(Long id) {
        return characterDndRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Personagem DnD não encontrado: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CharacterDnd> findAll() {
        return characterDndRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        if (!characterDndRepository.existsById(id)) {
            throw new BusinessException("Personagem DnD não encontrado: " + id);
        }
        characterDndRepository.deleteById(id);
    }
}

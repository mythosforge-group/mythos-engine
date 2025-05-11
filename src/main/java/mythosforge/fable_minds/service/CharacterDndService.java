package mythosforge.fable_minds.service;

import java.util.List;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mythosforge.fable_minds.models.CharacterDnd;
import mythosforge.fable_minds.repository.CharacterDndRepository;
import mythosforge.fable_minds.service.CharacterDndService;
import mythosforge.fable_minds.service.interfaces.ICharacterDndService;

@Service
@Transactional
public class CharacterDndService implements ICharacterDndService {

    private final CharacterDndRepository characterDndRepository;

    public CharacterDndService(CharacterDndRepository characterDndRepository) {
        this.characterDndRepository = characterDndRepository;
    }

    @Override
    public CharacterDnd create(CharacterDnd Character) {
        return characterDndRepository.save(Character);
    }

    @Override
    public CharacterDnd update(Long id, CharacterDnd Character) {
        CharacterDnd existing = characterDndRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Character DnD não encontrado: " + id));
        existing.setNome(Character.getNome());
        existing.setHistoria(Character.getHistoria());
        existing.setNivel(Character.getNivel());
        existing.setXp(Character.getXp());
        existing.setCampanha(Character.getCampanha());
        existing.setRaca(Character.getRaca());
        existing.setCharacterClass(Character.getCharacterClass());
        existing.setForca(Character.getForca());
        existing.setDestreza(Character.getDestreza());
        existing.setConstituicao(Character.getConstituicao());
        existing.setInteligencia(Character.getInteligencia());
        existing.setSabedoria(Character.getSabedoria());
        existing.setCarisma(Character.getCarisma());
        return characterDndRepository.save(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public CharacterDnd findById(Long id) {
        return characterDndRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Character DnD não encontrado: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CharacterDnd> findAll() {
        return characterDndRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        if (!characterDndRepository.existsById(id)) {
            throw new EntityNotFoundException("Character DnD não encontrado: " + id);
        }
        characterDndRepository.deleteById(id);
    }
}
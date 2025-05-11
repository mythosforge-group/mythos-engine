package mythosforge.fable_minds.service.interfaces;

import java.util.List;
import mythosforge.fable_minds.models.CharacterClass;

public interface ICharacterClassService {
    CharacterClass create(CharacterClass characterClass);
    CharacterClass update(Long id, CharacterClass characterClass);
    CharacterClass findById(Long id);
    List<CharacterClass> findAll();
    List<CharacterClass> findBySystemId(Long systemId);
    void delete(Long id);
}
package mythosforge.fable_minds.service.interfaces;

import java.util.List;
import mythosforge.fable_minds.models.Character;

public interface ICharacterService {
    Character create(Character character);
    Character update(Long id, Character character);
    Character findById(Long id);
    List<Character> findAll();
    void delete(Long id);
}
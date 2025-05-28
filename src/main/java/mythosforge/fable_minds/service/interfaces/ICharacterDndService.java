package mythosforge.fable_minds.service.interfaces;

import java.util.List;
import mythosforge.fable_minds.models.CharacterDnd;

public interface ICharacterDndService {
    CharacterDnd create(CharacterDnd character);
    CharacterDnd update(Long id, CharacterDnd character);
    CharacterDnd findById(Long id);
    List<CharacterDnd> findAll();
    void delete(Long id);
}
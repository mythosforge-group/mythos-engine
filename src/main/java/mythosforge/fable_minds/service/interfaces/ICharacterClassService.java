package mythosforge.fable_minds.service.interfaces;

import java.util.List;

import mythosforge.fable_minds.dto.CharacterClassDTO;
import mythosforge.fable_minds.models.CharacterClass;

public interface ICharacterClassService {
    CharacterClass create(CharacterClass characterClass);
    CharacterClass update(Long id, CharacterClass characterClass);
    CharacterClassDTO findByIdDto(Long id);
    List<CharacterClassDTO> findAllDto();
    List<CharacterClassDTO> findBySystemIdDto(Long systemId);
    void delete(Long id);
    CharacterClass findById(Long id);

}
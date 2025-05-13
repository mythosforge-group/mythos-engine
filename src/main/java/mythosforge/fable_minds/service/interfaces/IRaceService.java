package mythosforge.fable_minds.service.interfaces;

import java.util.List;

import mythosforge.fable_minds.config.security.auhentication.dto.RaceDTO;
import mythosforge.fable_minds.models.Race;

public interface IRaceService {
    Race create(Race race);
    Race update(Long id, Race race);
    RaceDTO findByIdDto(Long id);
    List<RaceDTO> findAllDto();
    List<RaceDTO> findBySystemIdDto(Long systemId);
    void delete(Long id);
}

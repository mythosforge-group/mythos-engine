package mythosforge.fable_minds.service.interfaces;

import java.util.List;
import mythosforge.fable_minds.models.Race;

public interface IRaceService {
    Race create(Race race);
    Race update(Long id, Race race);
    Race findById(Long id);
    List<Race> findAll();
    List<Race> findBySystemId(Long systemId);
    void delete(Long id);
}

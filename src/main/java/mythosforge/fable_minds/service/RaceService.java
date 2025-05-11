package mythosforge.fable_minds.service;

import java.util.List;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mythosforge.fable_minds.models.Race;
import mythosforge.fable_minds.repository.RaceRepository;
import mythosforge.fable_minds.service.RaceService;
import mythosforge.fable_minds.service.interfaces.IRaceService;

@Service
@Transactional
public class RaceService implements IRaceService {

    private final RaceRepository raceRepository;

    public RaceService(RaceRepository raceRepository) {
        this.raceRepository = raceRepository;
    }

    @Override
    public Race create(Race race) {
        return raceRepository.save(race);
    }

    @Override
    public Race update(Long id, Race race) {
        Race existing = raceRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Race not found: " + id));
        existing.setName(race.getName());
        existing.setDescription(race.getDescription());
        existing.setSystem(race.getSystem());
        // Se desejar gerenciar characters, trate aqui...
        return raceRepository.save(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public Race findById(Long id) {
        return raceRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Race not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Race> findAll() {
        return raceRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Race> findBySystemId(Long systemId) {
        return raceRepository.findBySystemId(systemId);
    }

    @Override
    public void delete(Long id) {
        if (!raceRepository.existsById(id)) {
            throw new EntityNotFoundException("Race not found: " + id);
        }
        raceRepository.deleteById(id);
    }
}
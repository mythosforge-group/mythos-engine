package mythosforge.fable_minds.service;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mythosforge.fable_minds.config.security.auhentication.dto.RaceDTO;
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
    public RaceDTO findByIdDto(Long id) {
        Race r = raceRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Race not found: " + id));
        return new RaceDTO(r.getId(), r.getName(), r.getDescription(), r.getSystem().getId());
    }

    @Override
    public List<RaceDTO> findAllDto() {
        return raceRepository.findAll()
            .stream()
            .map(r -> new RaceDTO(r.getId(), r.getName(), r.getDescription(), r.getSystem().getId()))
            .collect(Collectors.toList());
    }

    @Override
    public List<RaceDTO> findBySystemIdDto(Long systemId) {
        return raceRepository.findBySystemId(systemId)
            .stream()
            .map(r -> new RaceDTO(r.getId(), r.getName(), r.getDescription(), r.getSystem().getId()))
            .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        if (!raceRepository.existsById(id)) {
            throw new EntityNotFoundException("Race not found: " + id);
        }
        raceRepository.deleteById(id);
    }
}
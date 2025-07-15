package mythosforge.fable_minds.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mythosforge.fable_minds.models.Race;
import mythosforge.fable_minds.repository.RaceRepository;
import mythosforge.fable_minds.service.interfaces.IRaceService;
import mythosforge.fable_minds.dto.RaceDTO;
import mythosforge.fable_minds.exceptions.BusinessException;

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
                .orElseThrow(() -> new BusinessException("Raça não encontrada: " + id));
        existing.setName(race.getName());
        existing.setDescription(race.getDescription());
        existing.setSystem(race.getSystem());
        return raceRepository.save(existing);
    }

    @Override
    public Race findById(Long id) {
        return raceRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Raça não encontrada: " + id));
    }

    @Override
    public RaceDTO findByIdDto(Long id) {
        Race r = raceRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Raça não encontrada: " + id));
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
            throw new BusinessException("Raça não encontrada: " + id);
        }
        raceRepository.deleteById(id);
    }
}

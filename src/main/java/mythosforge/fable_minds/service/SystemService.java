package mythosforge.fable_minds.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mythosforge.fable_minds.models.System;
import mythosforge.fable_minds.repository.SystemRepository;
import mythosforge.fable_minds.service.interfaces.ISystemService;
import mythosforge.fable_minds.dto.SystemDTO;
import mythosforge.fable_minds.exceptions.BusinessException;

@Service
@Transactional
public class SystemService implements ISystemService {

    private final SystemRepository repo;

    public SystemService(SystemRepository repo) {
        this.repo = repo;
    }

    @Override
    public System create(System system) {
        return repo.save(system);
    }

    @Override
    public System update(Long id, System system) {
        System existing = repo.findById(id)
                .orElseThrow(() -> new BusinessException("Sistema não encontrado: " + id));
        existing.setName(system.getName());
        existing.setDescription(system.getDescription());
        return repo.save(existing);
    }

    @Override
    public SystemDTO findByIdDto(Long id) {
        System s = repo.findById(id)
                .orElseThrow(() -> new BusinessException("Sistema não encontrado: " + id));
        return new SystemDTO(s.getId(), s.getName(), s.getDescription());
    }

    @Override
    public List<SystemDTO> findAllDto() {
        return repo.findAll()
                .stream()
                .map(s -> new SystemDTO(s.getId(), s.getName(), s.getDescription()))
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new BusinessException("Sistema não encontrado: " + id);
        }
        repo.deleteById(id);
    }
}

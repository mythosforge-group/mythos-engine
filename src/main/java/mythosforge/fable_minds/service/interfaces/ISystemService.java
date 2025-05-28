package mythosforge.fable_minds.service.interfaces;

import java.util.List;

import mythosforge.fable_minds.config.security.auhentication.dto.SystemDTO;
import mythosforge.fable_minds.models.System;

public interface ISystemService {
    System create(System system);
    System update(Long id, System system);
    SystemDTO findByIdDto(Long id);
    List<SystemDTO> findAllDto();
    void delete(Long id);
}

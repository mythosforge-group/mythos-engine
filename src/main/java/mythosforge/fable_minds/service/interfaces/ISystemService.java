package mythosforge.fable_minds.service.interfaces;

import java.util.List;
import mythosforge.fable_minds.models.System;

public interface ISystemService {
    System create(System system);
    System update(Long id, System system);
    System findById(Long id);
    List<System> findAll();
    void delete(Long id);
}

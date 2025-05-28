package mythosforge.fable_minds.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import mythosforge.fable_minds.models.System;

public interface SystemRepository extends JpaRepository<System, Long> {
    Optional<System> findByName(String name);
}

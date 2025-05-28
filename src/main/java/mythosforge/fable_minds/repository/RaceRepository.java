package mythosforge.fable_minds.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import mythosforge.fable_minds.models.Race;

public interface RaceRepository extends JpaRepository<Race, Long> {
    List<Race> findBySystemId(Long systemId);
}
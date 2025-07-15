package mythosforge.chronicle_architect.repository;

import mythosforge.chronicle_architect.model.Rulebook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RulebookRepository extends JpaRepository<Rulebook, Long> {}
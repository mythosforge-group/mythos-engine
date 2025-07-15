package mythosforge.chronicle_architect.repository;

import mythosforge.chronicle_architect.models.RuleComponent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RuleComponentRepository extends JpaRepository<RuleComponent, Long> {}
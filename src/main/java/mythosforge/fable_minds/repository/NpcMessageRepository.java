package mythosforge.fable_minds.repository;

import mythosforge.fable_minds.models.NpcMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NpcMessageRepository extends JpaRepository<NpcMessage, Long> {}
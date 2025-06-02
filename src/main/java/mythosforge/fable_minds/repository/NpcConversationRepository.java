package mythosforge.fable_minds.repository;

import mythosforge.fable_minds.models.NpcConversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NpcConversationRepository extends JpaRepository<NpcConversation, Long> {}
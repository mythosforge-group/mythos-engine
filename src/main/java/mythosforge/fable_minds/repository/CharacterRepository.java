package mythosforge.fable_minds.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import mythosforge.fable_minds.models.Character;

public interface CharacterRepository extends JpaRepository<Character, Long> {
    List<Character> findByCampanhaId(Long campaignId);
}
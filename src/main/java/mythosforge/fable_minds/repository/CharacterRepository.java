package mythosforge.fable_minds.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import mythosforge.fable_minds.models.Character;

public interface CharacterRepository extends JpaRepository<Character, Long> {}
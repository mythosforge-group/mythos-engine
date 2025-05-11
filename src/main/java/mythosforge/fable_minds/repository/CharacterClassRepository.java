package mythosforge.fable_minds.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import mythosforge.fable_minds.models.CharacterClass;

public interface CharacterClassRepository extends JpaRepository<CharacterClass, Long> {
    List<CharacterClass> findBySystemId(Long systemId);
}
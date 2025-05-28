package mythosforge.fable_minds.repository;

import mythosforge.fable_minds.models.ArvoreGenealogica;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArvoreGenealogicaRepository extends JpaRepository<ArvoreGenealogica, Long> {
    Optional<ArvoreGenealogica> findByPersonagemId(Long personagemId);
}


package mythosforge.fable_minds.repository;

import mythosforge.fable_minds.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<Users> findByUsername(String username);
    Optional<Users> findByEmail(String email);
}
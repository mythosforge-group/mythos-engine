package mythosengine.security.service.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import mythosengine.security.service.auth.models.Users;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<Users> findByUsername(String username);
    Optional<Users> findByEmail(String email);
}
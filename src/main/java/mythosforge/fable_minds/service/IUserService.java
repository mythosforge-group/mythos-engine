package mythosforge.fable_minds.service;

import mythosforge.fable_minds.models.Users;

import java.util.List;
import java.util.Optional;

public interface IUserService {
    Users createUser(Users user);
    Optional<Users> getUserById(Long id);
    List<Users> getAllUsers();
    Users updateUser(Long id, Users userDetails);
    void deleteUser(Long id);
}

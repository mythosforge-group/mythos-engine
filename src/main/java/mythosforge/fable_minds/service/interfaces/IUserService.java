package mythosforge.fable_minds.service.interfaces;

import java.util.List;
import java.util.Optional;

import mythosengine.security.dto.UpdateUserDTO;
import mythosengine.security.service.auth.models.Users;

public interface IUserService {
    Users createUser(Users user);
    Optional<Users> getUserById(Long id);
    List<Users> getAllUsers();
    Users updateUser(Long id, UpdateUserDTO userDTO);
    void deleteUser(Long id);
}

package mythosforge.fable_minds.service;



import mythosforge.fable_minds.exceptions.BusinessException;
import mythosforge.fable_minds.service.interfaces.IUserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import mythosengine.config.security.authentication.dto.UpdateUserDTO;
import mythosengine.config.security.authentication.service.auth.models.Users;
import mythosengine.config.security.authentication.service.auth.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements IUserService {

        @Autowired
        private final UserRepository userRepository;
        @Autowired
        private final PasswordEncoder passwordEncoder;

        public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
            this.userRepository = userRepository;
            this.passwordEncoder = passwordEncoder;
        }

        @Override
        public Users createUser(Users user) {
            if (userRepository.existsByUsername(user.getUsername())) {
                throw new BusinessException("Nome de usuário já está em uso");
            }
            if (userRepository.existsByEmail(user.getEmail())) {
                throw new BusinessException("E-mail já está em uso");
            }
            user.setPassword(passwordEncoder.encode(user.getPassword())); // Codifica a senha
            return userRepository.save(user);
        }

        @Override
        public Optional<Users> getUserById(Long id) {
            return userRepository.findById(id);
        }

        @Override
        public List<Users> getAllUsers() {
            return userRepository.findAll();
        }

        @Override
        public Users updateUser(Long id, UpdateUserDTO userDTO) {
            return userRepository.findById(id).map(user -> {
                user.setUsername(userDTO.getUsername());
                user.setEmail(userDTO.getEmail());

                if (userDTO.getPassword() != null && !userDTO.getPassword().isBlank()) {
                    user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
                }

                return userRepository.save(user);
            }).orElseThrow(() -> new BusinessException("Usuário não encontrado com id: " + id));
        }

        @Override
        public void deleteUser(Long id) {
            if (!userRepository.existsById(id)) {
                throw new BusinessException("Usuário com ID " + id + " não encontrado.");
            }
            userRepository.deleteById(id);
        }
    }



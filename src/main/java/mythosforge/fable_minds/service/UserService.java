package mythosforge.fable_minds.service;



import mythosforge.fable_minds.models.User;
import mythosforge.fable_minds.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements IUserService {

        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;

        public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
            this.userRepository = userRepository;
            this.passwordEncoder = passwordEncoder;
        }

        @Override
        public User createUser(User user) {
            user.setPassword(passwordEncoder.encode(user.getPassword())); // Codifique a senha
            return userRepository.save(user);
        }

        @Override
        public Optional<User> getUserById(Long id) {
            return userRepository.findById(id);
        }

        @Override
        public List<User> getAllUsers() {
            return userRepository.findAll();
        }

        @Override
        public User updateUser(Long id, User userDetails) {
            return userRepository.findById(id).map(user -> {
                user.setUsername(userDetails.getUsername());
                user.setEmail(userDetails.getEmail());
                user.setPassword(userDetails.getPassword());
                return userRepository.save(user);
            }).orElseThrow(() -> new RuntimeException("Usuário não encontrado com id: " + id));
        }

        @Override
        public void deleteUser(Long id) {
            userRepository.deleteById(id);
        }
    }



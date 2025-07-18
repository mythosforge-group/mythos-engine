package mythosforge.fable_minds.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mythosengine.security.dto.UpdateUserDTO;
import mythosengine.security.dto.UserRegisterDTO;
import mythosengine.security.service.auth.models.Users;
import mythosforge.fable_minds.models.ErrorResponse;
import mythosforge.fable_minds.service.UserService;

/* ENDPOINTS
POST /api/users – registrar novo usuário
GET /api/users/{id} – buscar usuário por ID
GET /api/users – listar todos os usuários
PUT /api/users/{id} – atualizar usuário
DELETE /api/users/{id} – excluir usuário
*/

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<?> registerUser(@RequestBody UserRegisterDTO userRegisterDTO) {
        try {
            Users user = new Users();
            user.setUsername(userRegisterDTO.getUsername());
            user.setEmail(userRegisterDTO.getEmail());
            user.setPassword(userRegisterDTO.getPassword());

            Users created = userService.createUser(user);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Erro ao registrar usuário.", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Users> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Users>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable Long id, @RequestBody UpdateUserDTO userDTO) {
        try {
            Users updatedUser = userService.updateUser(id, userDTO);
            Map<String, Object> response = new HashMap<>();
            response.put("id", updatedUser.getId());
            response.put("username", updatedUser.getUsername());
            response.put("email", updatedUser.getEmail());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().header("message", e.getMessage()).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}

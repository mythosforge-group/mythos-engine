package mythosengine.config.security.authentication.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mythosengine.config.security.authentication.dto.LoginRequestDTO;
import mythosengine.config.security.authentication.service.AuthenticationService;
import mythosengine.config.security.authentication.service.JwtService;
import mythosengine.config.security.authentication.service.auth.models.Users;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService, JwtService jwtService) {
        this.authenticationService = authenticationService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequestDTO request) {
        String token = authenticationService.authenticate(request.getUsername(), request.getPassword());
        Long userId = jwtService.extractUserId(token);
        Users user = authenticationService.getUserById(userId);
    
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", Map.of(
            "id", user.getId(),
            "username", user.getUsername(),
            "email", user.getEmail()
        ));
    
        return ResponseEntity.ok(response);
    }    
}

package mythosengine.config.security.authentication.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequestDTO {
    @Size(min = 1, max = 50, message = "Username must be between 1 and 50 characters long")
    private String username;
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;
}

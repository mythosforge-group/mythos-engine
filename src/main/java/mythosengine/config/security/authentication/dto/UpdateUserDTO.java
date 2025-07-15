package mythosengine.config.security.authentication.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateUserDTO {
    private String username;
    private String email;
    private String password;
}
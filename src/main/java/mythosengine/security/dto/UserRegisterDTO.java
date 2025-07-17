package mythosengine.security.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserRegisterDTO {
    private String username;
    private String email;
    private String password;
}

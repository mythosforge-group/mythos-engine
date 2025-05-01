package mythosforge.fable_minds.config.security.auhentication.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserRegisterDTO {
    private String username;
    private String email;
    private String password;
}

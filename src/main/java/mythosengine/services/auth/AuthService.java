package mythosengine.services.auth;


import mythosengine.core.entity.Entity;
import mythosengine.core.persistence.PersistencePort;
import org.springframework.stereotype.Service;


@Service
public class AuthService {

    private final PersistencePort persistencePort;


    public AuthService(PersistencePort persistencePort) {
        this.persistencePort = persistencePort;
        System.out.println("SERVICE: Serviço de Autenticação inicializado.");
    }

    public Entity registerUser(String username, String password) {
        Entity user = new Entity("User");
        user.addProperty("username", username);

        user.addProperty("hashed_password", password.hashCode());

        persistencePort.save(user);
        System.out.println("AUTH_SERVICE: Usuário '" + username + "' registrado com ID " + user.getId());
        return user;
    }
}

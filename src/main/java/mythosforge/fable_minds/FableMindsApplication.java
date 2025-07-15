package mythosforge.fable_minds;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
        "mythosforge.fable_minds",
        "mythosengine"
})
@EnableJpaRepositories(basePackages = {
        "mythosforge.fable_minds.repository", 
        "mythosengine.config.security.authentication.service.auth.repository"
})
@EntityScan(basePackages = {
        "mythosforge.fable_minds.models", 
        "mythosengine.config.security.authentication.service.auth.models",
        "mythosengine.core.entity"
})
public class FableMindsApplication {

    public static void main(String[] args) {
        SpringApplication.run(FableMindsApplication.class, args);
    }

}
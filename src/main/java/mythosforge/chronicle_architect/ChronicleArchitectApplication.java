package mythosforge.chronicle_architect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
        "mythosforge.chronicle_architect",
        "mythosengine"
})
@EnableJpaRepositories(basePackages = {
        "mythosforge.chronicle_architect.repository",
        "mythosengine.security.service.auth.repository"
})
@EntityScan(basePackages = {
        "mythosforge.chronicle_architect.models",
        "mythosengine.security.service.auth.models",
        "mythosengine.core.entity"
})
public class ChronicleArchitectApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChronicleArchitectApplication.class, args);
    }
}
package mythosforge.lore_weaver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
        "mythosforge.lore_weaver",
        "mythosengine"
})
@EnableJpaRepositories(basePackages = {
        "mythosforge.lore_weaver.repository",
        "mythosengine.security.service.auth.repository"
})
@EntityScan(basePackages = {
        "mythosforge.lore_weaver.models",
        "mythosengine.security.service.auth.models",
        "mythosengine.core.entity"
})
public class LoreWeaverApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoreWeaverApplication.class, args);
    }
}
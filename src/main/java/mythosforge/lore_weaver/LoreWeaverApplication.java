package mythosforge.lore_weaver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
        "mythosforge.lore_weaver", // Escaneia apenas componentes da sua própria aplicação
        "mythosengine"             // e os componentes do framework
})
@EnableJpaRepositories(basePackages = {"mythosforge.lore_weaver"}) // Escaneia apenas seus próprios repositórios
@EntityScan(basePackages = {"mythosforge.lore_weaver"}) // Escaneia apenas suas próprias entidades
public class LoreWeaverApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoreWeaverApplication.class, args);
    }

}
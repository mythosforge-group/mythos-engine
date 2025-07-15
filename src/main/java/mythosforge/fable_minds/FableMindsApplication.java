package mythosforge.fable_minds;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Ponto de entrada principal da aplicação.
 * As anotações foram expandidas para garantir que o Spring encontre todos os
 * componentes, entidades JPA e repositórios em todos os módulos do projeto.
 */
@SpringBootApplication(scanBasePackages = {
		"mythosforge.fable_minds",  // Escaneia componentes gerais (@Service, @Controller) em todos os módulos da aplicação.
		"mythosengine"  // Escaneia todos os componentes do framework base.
})
@EnableJpaRepositories(basePackages = {"mythosforge.fable_minds"}) // FORÇA o scan de repositórios JPA em todos os módulos 'mythosforge'.
@EntityScan(basePackages = {"mythosforge.fable_minds"}) // FORÇA o scan de entidades (@Entity) em todos os módulos 'mythosforge'.
public class FableMindsApplication {

	public static void main(String[] args) {
		SpringApplication.run(FableMindsApplication.class, args);
	}

}

package mythosforge.fable_minds;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
    "mythosforge.fable_minds",
    "mythosengine.core"
})

public class FableMindsApplication {

	public static void main(String[] args) {
		SpringApplication.run(FableMindsApplication.class, args);
	}
}

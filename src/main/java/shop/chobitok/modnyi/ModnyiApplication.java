package shop.chobitok.modnyi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
@Configuration
@EnableJpaAuditing
@EnableScheduling
@EnableWebSecurity
public class ModnyiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ModnyiApplication.class, args);
	}

}

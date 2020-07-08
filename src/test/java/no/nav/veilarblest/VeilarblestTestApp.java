package no.nav.veilarblest;

import no.nav.veilarblest.config.ApplicationTestConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Import;


@EnableAutoConfiguration
@Import(ApplicationTestConfig.class)
public class VeilarblestTestApp {

    public static void main(String... args) {
        SpringApplication application = new SpringApplication(VeilarblestTestApp.class);
        application.setAdditionalProfiles("local");
        application.run(args);
    }
}

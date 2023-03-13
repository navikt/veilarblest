package no.nav.veilarblest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;


@EnableAutoConfiguration
@ComponentScan(basePackages = {"no.nav.veilarblest.config", "no.nav.veilarblest.kafka"})
public class VeilarblestTestApp {

    public static void main(String... args) {
        SpringApplication application = new SpringApplication(VeilarblestTestApp.class);
        application.setAdditionalProfiles("local");
        application.run(args);
    }
}

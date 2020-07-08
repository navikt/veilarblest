package no.nav.veilarblest;

import no.nav.common.utils.SslUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class VeilarblestApp {
    public static void main(String... args) {
        SslUtils.setupTruststore();
        SpringApplication.run(VeilarblestApp.class, args);

    }
}

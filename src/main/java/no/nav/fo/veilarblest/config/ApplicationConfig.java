package no.nav.fo.veilarblest.config;

import no.nav.apiapp.ApiApplication;
import no.nav.apiapp.ApiApplication.NaisApiApplication;
import no.nav.apiapp.config.ApiAppConfigurator;
import no.nav.fo.veilarblest.rest.LestRessurs;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        LestRessurs.class
})
public class ApplicationConfig implements NaisApiApplication {

    public static final String APPLICATION_NAME = "veilarblest";

    @Override
    public void configure(ApiAppConfigurator apiAppConfigurator) {
    }
}

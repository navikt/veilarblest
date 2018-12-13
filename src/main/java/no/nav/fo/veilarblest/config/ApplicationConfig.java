package no.nav.fo.veilarblest.config;

import no.nav.apiapp.ApiApplication.NaisApiApplication;
import no.nav.apiapp.config.ApiAppConfigurator;
import no.nav.dialogarena.aktor.AktorConfig;
import no.nav.fo.veilarblest.rest.LestRessurs;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.sql.DataSource;

import static no.nav.fo.veilarblest.config.DatabaseConfig.migrateDatabase;

@Configuration
@Import({
        DatabaseConfig.class,
        AktorConfig.class,
        CacheConfig.class,
        LestRessurs.class
})
public class ApplicationConfig implements NaisApiApplication {

    public static final String APPLICATION_NAME = "veilarblest";
    public static final String AKTOER_V2_URL_PROPERTY = "AKTOER_V2_ENDPOINTURL";
    public static final String VEILARBLOGIN_REDIRECT_URL_URL_PROPERTY = "VEILARBLOGIN_REDIRECT_URL_URL";
    public static final String VEILARBAZUREADPROXY_DISCOVERY_URL_PROPERTY = "VEILARBAZUREADPROXY_DISCOVERY_URL";
    public static final String AAD_B2C_CLIENTID_USERNAME_PROPERTY = "AAD_B2C_CLIENTID_USERNAME";
    public static final String AAD_B2C_CLIENTID_PASSWORD_PROPERTY = "AAD_B2C_CLIENTID_PASSWORD";

    @Inject
    private DataSource adminDataSource;

    @Override
    public void startup(ServletContext servletContext) {
        migrateDatabase(adminDataSource);
    }

    @Override
    public void configure(ApiAppConfigurator apiAppConfigurator) {
        apiAppConfigurator
                .sts()
                .azureADB2CLogin()
                .issoLogin();
    }

}

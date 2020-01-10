package no.nav.fo.veilarblest.config;

import no.nav.apiapp.ApiApplication;
import no.nav.apiapp.config.ApiAppConfigurator;
import no.nav.dialogarena.aktor.AktorConfig;
import no.nav.fo.veilarblest.rest.LestRessurs;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.sql.DataSource;

import static no.nav.apiapp.ServletUtil.leggTilServlet;
import static no.nav.fo.veilarblest.config.DatabaseConfig.migrateDatabase;

@Configuration
@Import({
        DatabaseConfig.class,
        AktorConfig.class,
        CacheConfig.class,
        LestRessurs.class
})
public class ApplicationConfig implements ApiApplication {

    public static final String APPLICATION_NAME = "veilarblest";
    public static final String AKTOER_V2_URL_PROPERTY = "AKTOER_V2_ENDPOINTURL";
    public static final String VEILARBLOGIN_REDIRECT_URL_URL_PROPERTY = "VEILARBLOGIN_REDIRECT_URL_URL";
    public static final String SECURITYTOKENSERVICE_URL = "SECURITYTOKENSERVICE_URL";

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

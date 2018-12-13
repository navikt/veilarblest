package no.nav.fo.veilarblest.config;

import no.nav.sbl.jdbc.DataSourceFactory;
import org.flywaydb.core.Flyway;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

import static no.nav.sbl.util.EnvironmentUtils.EnviromentClass.P;
import static no.nav.sbl.util.EnvironmentUtils.*;

@Configuration
public class DatabaseConfig {

    public static final String VEILARBLEST_DB_URL_PROPERTY = "VEILARBLEST_DB_URL";
    public static final String VEILARBLEST_DB_USER_PROPERTY = "VEILARBLEST_DB_USER";
    public static final String VEILARBLEST_DB_PASSWORD_PROPERTY = "VEILARBLEST_DB_PASSWORD";

    @Bean
    public DataSource dataSource() {
        return DataSourceFactory.dataSource()
                .url(getRequiredProperty(VEILARBLEST_DB_URL_PROPERTY))
                .username(getRequiredProperty(VEILARBLEST_DB_USER_PROPERTY))
                .password(getRequiredProperty(VEILARBLEST_DB_PASSWORD_PROPERTY))
                .build();
    }

    @Bean
    public DSLContext dslContext(DataSource dataSource) {
        return DSL.using(dataSource, SQLDialect.POSTGRES);
    }

    public static void migrateDatabase(DataSource dataSource) {
        boolean isProd = getEnvironmentClass() == P;
        String role = isProd ? "veilarblest-admin" : String.join("-", "veilarblest", requireEnvironmentName(), "admin");
        Flyway.configure()
                .dataSource(dataSource)
                .initSql(String.format("SET ROLE \"%s\"", role))
                .load()
                .migrate();
    }

}

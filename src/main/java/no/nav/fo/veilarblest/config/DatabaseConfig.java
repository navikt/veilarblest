package no.nav.fo.veilarblest.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.SneakyThrows;
import no.nav.fo.veilarblest.vault.HikariCPVaultUtil;
import org.flywaydb.core.Flyway;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

import static no.nav.fo.veilarblest.config.ApplicationConfig.APPLICATION_NAME;
import static no.nav.sbl.util.EnvironmentUtils.EnviromentClass.P;
import static no.nav.sbl.util.EnvironmentUtils.*;

@Configuration
public class DatabaseConfig {

    public static final String VEILARBLEST_DB_URL_PROPERTY = "VEILARBLEST_DB_URL";
    public static final String VEILARBLEST_DB_USER_PROPERTY = "VEILARBLEST_DB_USER";
    public static final String VEILARBLEST_DB_PASSWORD_PROPERTY = "VEILARBLEST_DB_PASSWORD";

    @Bean
    public DataSource adminDataSource() {
        return dataSource("admin");
    }

    @Bean
    public DataSource userDataSource() {
        return dataSource("user");
    }

    @Bean
    public DSLContext dslContext(DataSource userDataSource) {
        return DSL.using(userDataSource, SQLDialect.POSTGRES);
    }

    static void migrateDatabase(DataSource dataSource) {
        Flyway.configure()
                .dataSource(dataSource)
                .initSql(String.format("SET ROLE \"%s\"", dbRole("admin")))
                .load()
                .migrate();
    }

    @SneakyThrows
    private HikariDataSource dataSource(String user) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(getRequiredProperty(VEILARBLEST_DB_URL_PROPERTY));
        config.setMaximumPoolSize(300);
        config.setMinimumIdle(1);
        String mountPath = getEnvironmentClass() == P
                ? "postgresql/prod-fss"
                : "postgresql/preprod-fss";
        return HikariCPVaultUtil.createHikariDataSourceWithVaultIntegration(config, mountPath, dbRole(user));
    }

    private static String dbRole(String role) {
        return getEnvironmentClass() == P
                ? String.join("-", APPLICATION_NAME, role)
                : String.join("-", APPLICATION_NAME, requireEnvironmentName(), role);
    }

}

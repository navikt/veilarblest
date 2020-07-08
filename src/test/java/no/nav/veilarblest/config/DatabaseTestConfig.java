package no.nav.veilarblest.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import static no.nav.common.utils.EnvironmentUtils.getRequiredProperty;
import static no.nav.veilarblest.JooqGenerator.*;

@Configuration
public class DatabaseTestConfig {


    private final EnvironmentProperties environmentProperties;

    public DatabaseTestConfig(EnvironmentProperties environmentProperties) {
        this.environmentProperties = environmentProperties;
    }

    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(getRequiredProperty(VEILARBLEST_DB_URL_PROPERTY));
        config.setUsername(getRequiredProperty(VEILARBLEST_DB_USER_PROPERTY));
        config.setPassword(getRequiredProperty(VEILARBLEST_DB_PASSWORD_PROPERTY));
        config.setMaximumPoolSize(5);

        return new HikariDataSource(config);
    }

    @PostConstruct
    public void migrateDatabase() {
        Flyway.configure()
                .dataSource(dataSource())
                .load()
                .migrate();
    }


}

package no.nav.veilarblest.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.SneakyThrows;
import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.annotation.PostConstruct;
import javax.sql.DataSource;


@Configuration
@EnableTransactionManagement
public class DatabaseConfig {

    private final EnvironmentProperties environmentProperties;

    public DatabaseConfig(EnvironmentProperties environmentProperties) {
        this.environmentProperties = environmentProperties;
    }

    @Bean
    @SneakyThrows
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(environmentProperties.getDbUrl());
        config.setMaximumPoolSize(3);
        config.setMinimumIdle(1);
        return new HikariDataSource(config);
    }


    @PostConstruct
    public void migrateDatabase() {
        var dataSource = dataSource();
        Flyway.configure()
                .dataSource(dataSource)
                .load()
                .migrate();
    }


}

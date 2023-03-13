package no.nav.veilarblest.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@Slf4j
public class DatabaseTestConfig {



    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:tc:postgresql:15.2:///veilarblest");
        config.setMaximumPoolSize(5);
        HikariDataSource dataSource = new HikariDataSource(config);
        migrateDatabase(dataSource);

        return dataSource;
    }

    private void migrateDatabase(DataSource dataSource) {
        Flyway.configure()
                .dataSource(dataSource)
                .load()
                .migrate();
    }
}

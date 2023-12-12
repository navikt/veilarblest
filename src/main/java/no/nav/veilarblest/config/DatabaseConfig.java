package no.nav.veilarblest.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.SneakyThrows;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.annotation.PostConstruct;
import javax.sql.DataSource;


@Configuration
@EnableTransactionManagement
public class DatabaseConfig {
    private  DataSource dataSource;
    @Bean
    @SneakyThrows
    public DataSource dataSource(@Value("NAIS_DATABASE_VEILARBLEST_VEILARBLEST_URL") String urlWithCreds) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(urlWithCreds);
        config.setMaximumPoolSize(3);
        config.setMinimumIdle(1);
        dataSource = new HikariDataSource(config);
        return  dataSource;
    }


    @PostConstruct
    public void migrateDatabase() {
        Flyway.configure()
                .dataSource(dataSource)
                .load()
                .migrate();
    }
}

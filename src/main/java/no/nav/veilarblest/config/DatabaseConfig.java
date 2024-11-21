package no.nav.veilarblest.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.SneakyThrows;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;


@Configuration
@EnableTransactionManagement
public class DatabaseConfig {

    @Bean
    @SneakyThrows
    public DataSource dataSource(
            @Value("${db.url}") String url,
            @Value("${db.username}") String username,
            @Value("${db.password}") String password) {


        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(3);
        config.setMinimumIdle(1);
        config.setDriverClassName("org.postgresql.Driver");

        HikariDataSource dataSource;

        // Setter opp datasource i try/catch fordi hikari logger url som inneholder passord dersom det feiler
        try {
            dataSource = new HikariDataSource(config);
        } catch(RuntimeException e) {
            throw new RuntimeException("Kunne ikke sette opp hikari datasource");
        }

        Flyway.configure()
                .dataSource(dataSource)
                .load()
                .migrate();

        return dataSource;
    }
}

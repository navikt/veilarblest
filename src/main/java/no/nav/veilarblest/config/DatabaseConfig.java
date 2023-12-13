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
            @Value("${db.host}") String host,
            @Value("${db.port}") String port,
            @Value("${db.database}") String database,
            @Value("${db.username}") String username,
            @Value("${db.password}") String password) {


        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://" + host + ":" + port + "/" + database);
        config.setUsername(username);
        config.setPassword(password);

        config.setMaximumPoolSize(3);
        config.setMinimumIdle(1);

        HikariDataSource dataSource = new HikariDataSource(config);

        Flyway.configure()
                .dataSource(dataSource)
                .load()
                .migrate();

        return dataSource;
    }
}

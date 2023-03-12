package no.nav.veilarblest.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.SneakyThrows;
import no.nav.vault.jdbc.hikaricp.HikariCPVaultUtil;
import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import static no.nav.veilarblest.config.ApplicationConfig.APPLICATION_NAME;

@Configuration
@EnableTransactionManagement
@Profile("!local")
public class DatabaseConfig {

    private final EnvironmentProperties environmentProperties;

    public DatabaseConfig(EnvironmentProperties environmentProperties) {
        this.environmentProperties = environmentProperties;
    }

    @Bean
    public DataSource dataSource() {
        return dataSource("user");
    }


    @SneakyThrows
    private HikariDataSource dataSource(String user) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(environmentProperties.getDbUrl());
        config.setMaximumPoolSize(3);
        config.setMinimumIdle(1);
        String mountPath = environmentProperties.getEnvironmentName().toLowerCase().equals("p")
                ? "postgresql/prod-fss"
                : "postgresql/preprod-fss";
        return HikariCPVaultUtil.createHikariDataSourceWithVaultIntegration(config, mountPath, dbRole(user));
    }

    private String dbRole(String role) {
        return environmentProperties.getEnvironmentName().toLowerCase().equals("p")
                ? String.join("-", APPLICATION_NAME, role)
                : String.join("-", APPLICATION_NAME, environmentProperties.getEnvironmentName(), role);
    }


    @PostConstruct
    public void migrateDatabase() {
        var dataSource = dataSource("admin");
        Flyway.configure()
                .dataSource(dataSource)
                .initSql(String.format("SET ROLE \"%s\"", dbRole("admin")))
                .load()
                .migrate();
    }


}

package no.nav.veilarblest;

import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.sql.SQLException;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("local")
public abstract class SpringBootTestBase {

    static EmbeddedPostgres postgres;

    static {
        try {
            postgres = EmbeddedPostgres.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) throws SQLException {
        registry.add("db.url", () ->  postgres.getJdbcUrl("postgres", "veilarblest"));
        registry.add("db.username", () -> "postgres");
        registry.add("db.password", () -> "postgres");
    }


    @Autowired
    protected EmbeddedKafkaBroker kafkaBroker;

    static {
        System.setProperty("VEILARBLEST_DB_URL","jdbc:postgresql://localhost/veilarblest");
        System.setProperty("VEILARBLEST_DB_USER","postgres");
        System.setProperty("VEILARBLEST_DB_PASSWORD","1234");
    }

    @LocalServerPort
    protected int port;

}
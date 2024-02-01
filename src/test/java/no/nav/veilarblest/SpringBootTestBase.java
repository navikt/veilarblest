package no.nav.veilarblest;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("local")
public abstract class SpringBootTestBase {

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("db.host", postgres::getHost);
        registry.add("db.port", postgres::getFirstMappedPort);
        registry.add("db.database", postgres::getDatabaseName);
        registry.add("db.username", postgres::getUsername);
        registry.add("db.password", postgres::getPassword);
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
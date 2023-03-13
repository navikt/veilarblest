package no.nav.veilarblest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.test.context.ActiveProfiles;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("local")
public abstract class SpringBootTestBase {


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
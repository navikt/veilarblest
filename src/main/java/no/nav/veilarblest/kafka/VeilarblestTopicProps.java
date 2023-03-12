package no.nav.veilarblest.kafka;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.kafka")
public class VeilarblestTopicProps {
    String veilederHarLestAkvititetsplanenTopicAiven;
}

package no.nav.veilarblest.kafka;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class FeiletKafkaMelding {
    long id;
    String topicName;
    String messageKey;
    String jsonPayload;
}

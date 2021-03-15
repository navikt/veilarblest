package no.nav.veilarblest.kafka;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.ZonedDateTime;

@Data
@Accessors(chain = true)
public class VeilederHarLestDTO {
    String aktorId;
    String veilederId;
    ZonedDateTime harLestTidspunkt;
}

package no.nav.veilarblest.rest.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class LestDto {
    public LocalDateTime tidspunkt;
    public String ressurs;
    public String verdi;
}

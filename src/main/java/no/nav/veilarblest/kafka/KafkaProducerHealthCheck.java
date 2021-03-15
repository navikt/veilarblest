package no.nav.veilarblest.kafka;

import lombok.RequiredArgsConstructor;
import no.nav.common.health.HealthCheck;
import no.nav.common.health.HealthCheckResult;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class KafkaProducerHealthCheck implements HealthCheck {
    private final FeiletKafkaMeldingRepository feiletKafkaMeldingRepository;

    @Override
    public HealthCheckResult checkHealth() {
        List<FeiletKafkaMelding> feiletKafkaMeldinger = feiletKafkaMeldingRepository.hentFeiledeKafkaMeldinger(1);
        if (feiletKafkaMeldinger.size() > 0) {
            return HealthCheckResult.unhealthy("Sending av Kafka-melding har feilet");
        }
        return HealthCheckResult.healthy();
    }
}

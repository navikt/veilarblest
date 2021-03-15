package no.nav.veilarblest.config;

import lombok.extern.slf4j.Slf4j;
import no.nav.common.health.selftest.SelfTestCheck;
import no.nav.common.health.selftest.SelfTestChecks;
import no.nav.veilarblest.kafka.KafkaProducerHealthCheck;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;

@Slf4j
@Configuration
public class HelsesjekkConfig {

    @Bean
    public SelfTestChecks selfTestChecks(KafkaProducerHealthCheck kafkaProducerHealthCheck) {
        List<SelfTestCheck> selfTestChecks = Collections.singletonList(
                new SelfTestCheck("Kafka producer", false, kafkaProducerHealthCheck)
        );

        return new SelfTestChecks(selfTestChecks);
    }
}

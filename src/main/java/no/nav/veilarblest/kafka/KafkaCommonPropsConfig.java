package no.nav.veilarblest.kafka;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Properties;

import static no.nav.common.kafka.util.KafkaPropertiesPreset.aivenByteProducerProperties;
import static no.nav.veilarblest.kafka.KafkaConfigCommon.PRODUCER_AIVEN_CLIENT_ID;

@Configuration
@Profile("!local")
public class KafkaCommonPropsConfig {

    @Bean
    @Qualifier("kafka")
    public Properties kafkaCommonProps() {
        return aivenByteProducerProperties(PRODUCER_AIVEN_CLIENT_ID);
    }
}

package no.nav.veilarblest.config;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.EmbeddedKafkaZKBroker;

import java.util.Properties;

import static no.nav.veilarblest.kafka.KafkaConfigCommon.PRODUCER_AIVEN_CLIENT_ID;

@Configuration
public class KafkaTestConfig {
    @Bean
    @Qualifier("kafka")
    public Properties kafkaCommonProps(EmbeddedKafkaBroker broker) {
        var kafkaProps = new Properties();
        kafkaProps.put(ProducerConfig.CLIENT_ID_CONFIG, PRODUCER_AIVEN_CLIENT_ID);
        kafkaProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class);
        kafkaProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class);
        kafkaProps.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, broker.getBrokersAsString());
        return kafkaProps;
    }

    @Bean
    EmbeddedKafkaBroker embeddedKafka() {
        return new EmbeddedKafkaZKBroker(1, true, 1);
    }

}

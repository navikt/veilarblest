package no.nav.veilarblest.kafka;

import no.nav.common.utils.Credentials;
import no.nav.veilarblest.config.EnvironmentProperties;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.LoggingProducerListener;

import java.util.HashMap;

@EnableKafka
@Configuration
public class KafkaConfig {
    private final Credentials serviceUserCredentials;

    private final String brokersUrl;

    @Autowired
    public KafkaConfig(EnvironmentProperties properties, Credentials serviceUserCredentials) {
        brokersUrl = properties.getKafkaBrokersUrl();
        this.serviceUserCredentials = serviceUserCredentials;
    }

    @Bean
    public KafkaTopics kafkaTopics() {
        return KafkaTopics.create();
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        ProducerFactory<String, String> producerFactory = new DefaultKafkaProducerFactory<>(kafkaProducerProperties(brokersUrl, serviceUserCredentials));
        KafkaTemplate<String, String> template = new KafkaTemplate<>(producerFactory);
        LoggingProducerListener<String, String> producerListener = new LoggingProducerListener<>();
        producerListener.setIncludeContents(false);
        template.setProducerListener(producerListener);
        return template;
    }

    private static HashMap<String, Object> kafkaBaseProperties(String kafkaBrokersUrl, Credentials serviceUserCredentials) {
        HashMap<String, Object> props = new HashMap<>();
        props.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, kafkaBrokersUrl);
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
        props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
        props.put(SaslConfigs.SASL_JAAS_CONFIG, "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"" + serviceUserCredentials.username + "\" password=\"" + serviceUserCredentials.password + "\";");
        return props;
    }

    private static HashMap<String, Object> kafkaProducerProperties (String kafkaBrokersUrl, Credentials serviceUserCredentials) {
        HashMap<String, Object> props = kafkaBaseProperties(kafkaBrokersUrl, serviceUserCredentials);
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 3000);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "veilarblest-producer");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return props;
    }

}

package no.nav.veilarblest.kafka;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import no.nav.common.job.leader_election.LeaderElectionClient;
import no.nav.common.kafka.producer.KafkaProducerClient;
import no.nav.common.kafka.producer.feilhandtering.KafkaProducerRecordProcessor;
import no.nav.common.kafka.producer.feilhandtering.KafkaProducerRecordStorage;
import no.nav.common.kafka.producer.feilhandtering.KafkaProducerRepository;
import no.nav.common.kafka.producer.util.KafkaProducerClientBuilder;
import no.nav.common.kafka.spring.PostgresJdbcTemplateProducerRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Properties;

import static no.nav.common.kafka.util.KafkaPropertiesPreset.aivenByteProducerProperties;


@Configuration
@EnableConfigurationProperties({VeilarblestTopicProps.class})
@Slf4j
public class KafkaConfigCommon {

    public static final String PRODUCER_AIVEN_CLIENT_ID = "veilarblest-aiven-producer";

    private final KafkaProducerRecordProcessor aivenProducerRecordProcessor;

    private final KafkaProducerRecordStorage producerRecordStorage;

    @Bean
    @Qualifier("kafka")
    @Lazy
    @Profile("!local")
    public Properties kafkaCommonProps() {
        return aivenByteProducerProperties(PRODUCER_AIVEN_CLIENT_ID);
    }

    public KafkaConfigCommon(
            LeaderElectionClient leaderElectionClient,
            JdbcTemplate jdbcTemplate,
            VeilarblestTopicProps veilarblestTopicProps,
            MeterRegistry meterRegistry,
            @Qualifier("kafka") @Lazy Properties kafkaCommonProps
    ) {
        log.info("**********  Setter opp kafka producer med properties : {}", kafkaCommonProps);
        KafkaProducerRepository producerRepository = new PostgresJdbcTemplateProducerRepository(jdbcTemplate);

        producerRecordStorage = new KafkaProducerRecordStorage(producerRepository);

        KafkaProducerClient<byte[], byte[]> aivenProducerClient = KafkaProducerClientBuilder.<byte[], byte[]>builder()
                .withProperties(kafkaCommonProps)
                .withMetrics(meterRegistry)
                .build();

        aivenProducerRecordProcessor = new KafkaProducerRecordProcessor(
                producerRepository,
                aivenProducerClient,
                leaderElectionClient,
                List.of(
                        veilarblestTopicProps.getVeilederHarLestAkvititetsplanenTopicAiven()
                )
        );
    }

    @Bean
    public KafkaProducerRecordStorage producerRecordProcessor() {
        return producerRecordStorage;
    }



    @PostConstruct
    public void start() {
        aivenProducerRecordProcessor.start();
    }
}

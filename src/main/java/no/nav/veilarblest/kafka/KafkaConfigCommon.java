package no.nav.veilarblest.kafka;

import io.micrometer.core.instrument.MeterRegistry;
import no.nav.common.job.leader_election.LeaderElectionClient;
import no.nav.common.kafka.producer.KafkaProducerClient;
import no.nav.common.kafka.producer.feilhandtering.KafkaProducerRecordProcessor;
import no.nav.common.kafka.producer.feilhandtering.KafkaProducerRecordStorage;
import no.nav.common.kafka.producer.feilhandtering.KafkaProducerRepository;
import no.nav.common.kafka.producer.util.KafkaProducerClientBuilder;
import no.nav.common.kafka.spring.PostgresJdbcTemplateProducerRepository;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.PostConstruct;
import java.util.List;

import static no.nav.common.kafka.util.KafkaPropertiesPreset.aivenByteProducerProperties;


@Configuration
@EnableConfigurationProperties({KafkaProperties.class})
public class KafkaConfigCommon {

    public final static String PRODUCER_AIVEN_CLIENT_ID = "veilarblest-aiven-producer";

    private final KafkaProducerRecordProcessor aivenProducerRecordProcessor;

    private final KafkaProducerRecordStorage producerRecordStorage;


    public KafkaConfigCommon(
            LeaderElectionClient leaderElectionClient,
            JdbcTemplate jdbcTemplate,
            KafkaProperties kafkaProperties,
            MeterRegistry meterRegistry
    ) {
        KafkaProducerRepository producerRepository = new PostgresJdbcTemplateProducerRepository(jdbcTemplate);

        producerRecordStorage = new KafkaProducerRecordStorage(producerRepository);

        KafkaProducerClient<byte[], byte[]> aivenProducerClient = KafkaProducerClientBuilder.<byte[], byte[]>builder()
                .withProperties(aivenByteProducerProperties(PRODUCER_AIVEN_CLIENT_ID))
                .withMetrics(meterRegistry)
                .build();

        aivenProducerRecordProcessor = new KafkaProducerRecordProcessor(
                producerRepository,
                aivenProducerClient,
                leaderElectionClient,
                List.of(
                        kafkaProperties.getVeilederHarLestAkvititetsplanenTopicAiven()
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

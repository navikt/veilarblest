package no.nav.veilarblest.kafka;

import io.micrometer.core.instrument.MeterRegistry;
import no.nav.common.job.leader_election.LeaderElectionClient;
import no.nav.common.kafka.producer.KafkaProducerClient;
import no.nav.common.kafka.producer.feilhandtering.KafkaProducerRecordProcessor;
import no.nav.common.kafka.producer.feilhandtering.KafkaProducerRecordStorage;
import no.nav.common.kafka.producer.feilhandtering.KafkaProducerRepository;
import no.nav.common.kafka.producer.util.KafkaProducerClientBuilder;
import no.nav.common.kafka.spring.PostgresJdbcTemplateProducerRepository;
import no.nav.common.utils.Credentials;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.PostConstruct;
import java.util.List;

import static no.nav.common.kafka.util.KafkaPropertiesPreset.aivenDefaultProducerProperties;
import static no.nav.common.kafka.util.KafkaPropertiesPreset.onPremDefaultProducerProperties;


@Configuration
@EnableConfigurationProperties({KafkaProperties.class})
public class KafkaConfigCommon {

    public final static String PRODUCER_CLIENT_ID = "veilarblest-producer";

    private final KafkaProducerRecordProcessor aivenProducerRecordProcessor;

    private final KafkaProducerRecordProcessor onPremProducerRecordProcessor;

    private final KafkaProducerRecordStorage producerRecordStorage;


    public KafkaConfigCommon(
            LeaderElectionClient leaderElectionClient,
            JdbcTemplate jdbcTemplate,
            KafkaProperties kafkaProperties,
            Credentials credentials,
            MeterRegistry meterRegistry
    ) {
        KafkaProducerRepository producerRepository = new PostgresJdbcTemplateProducerRepository(jdbcTemplate);

        producerRecordStorage = new KafkaProducerRecordStorage(producerRepository);

        KafkaProducerClient<byte[], byte[]> onPremProducerClient = KafkaProducerClientBuilder.<byte[], byte[]>builder()
                .withProperties(onPremDefaultProducerProperties(PRODUCER_CLIENT_ID, kafkaProperties.getKafkaBrokersUrl(), credentials))
                .withMetrics(meterRegistry)
                .build();

        onPremProducerRecordProcessor = new KafkaProducerRecordProcessor(
                producerRepository,
                onPremProducerClient,
                leaderElectionClient,
                List.of(
                        kafkaProperties.getVeilederHarLestAktivitetsplanenTopicOnPrem()
                )
        );

        KafkaProducerClient<byte[], byte[]> aivenProducerClient = KafkaProducerClientBuilder.<byte[], byte[]>builder()
                .withProperties(aivenDefaultProducerProperties(PRODUCER_CLIENT_ID))
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
        onPremProducerRecordProcessor.start();
        aivenProducerRecordProcessor.start();
    }
}

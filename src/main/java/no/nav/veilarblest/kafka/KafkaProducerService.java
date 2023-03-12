package no.nav.veilarblest.kafka;

import lombok.extern.slf4j.Slf4j;
import no.nav.common.kafka.producer.feilhandtering.KafkaProducerRecordStorage;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static no.nav.common.json.JsonUtils.toJson;
import static no.nav.common.kafka.producer.util.ProducerUtils.serializeStringRecord;

@Service
@Slf4j
public class KafkaProducerService {

    private final KafkaProducerRecordStorage producerRecordStorage;

    private final VeilarblestTopicProps veilarblestTopicProps;

    @Autowired
    public KafkaProducerService(
            KafkaProducerRecordStorage producerRecordStorage,
            VeilarblestTopicProps veilarblestTopicProps
    ) {
        this.producerRecordStorage = producerRecordStorage;
        this.veilarblestTopicProps = veilarblestTopicProps;
    }

    public void publiserVeilederHarLestAktivitetPlanen(VeilederHarLestDTO veilederHarLestDTO) {
        log.info(String.format("Publisere veileder har lest aktivitet planen med key %s", veilederHarLestDTO.getAktorId()));
        store(veilarblestTopicProps.getVeilederHarLestAkvititetsplanenTopicAiven(), veilederHarLestDTO.getAktorId(), toJson(veilederHarLestDTO));
    }


    private void store(String topic, String key, String value) {
        ProducerRecord<byte[], byte[]> producerRecord = serializeStringRecord(new ProducerRecord<>(topic, key, value));
        producerRecordStorage.store(producerRecord);
    }

}

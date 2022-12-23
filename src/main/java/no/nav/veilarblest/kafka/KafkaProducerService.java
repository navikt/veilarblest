package no.nav.veilarblest.kafka;

import lombok.extern.slf4j.Slf4j;
import no.nav.common.kafka.producer.feilhandtering.KafkaProducerRecordStorage;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static no.nav.common.kafka.producer.util.ProducerUtils.serializeJsonRecord;

@Service
@Slf4j
public class KafkaProducerService {

    private final KafkaProducerRecordStorage producerRecordStorage;

    private final KafkaProperties kafkaProperties;

    @Autowired
    public KafkaProducerService(
            KafkaProducerRecordStorage producerRecordStorage,
            KafkaProperties kafkaProperties
    ) {
        this.producerRecordStorage = producerRecordStorage;
        this.kafkaProperties = kafkaProperties;
    }

    public void publiserVeilederHarLestAktivitetPlanen(VeilederHarLestDTO veilederHarLestDTO) {
        log.info(String.format("Publisere veileder har lest aktivitet planen med key %s", veilederHarLestDTO.getAktorId()));
        store(kafkaProperties.getVeilederHarLestAktivitetsplanenTopicOnPrem(), veilederHarLestDTO.getAktorId(), veilederHarLestDTO);
        store(kafkaProperties.getVeilederHarLestAkvititetsplanenTopicAiven(), veilederHarLestDTO.getAktorId(), veilederHarLestDTO);
    }


    private void store(String topic, String key, Object value) {
        ProducerRecord<byte[], byte[]> record = serializeJsonRecord(new ProducerRecord<>(topic, key, value));
        producerRecordStorage.store(record);
    }

}

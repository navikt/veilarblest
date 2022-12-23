package no.nav.veilarblest.kafka;

import no.nav.common.kafka.producer.feilhandtering.KafkaProducerRecordStorage;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static no.nav.common.kafka.producer.util.ProducerUtils.serializeJsonRecord;

@Service
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
        store(kafkaProperties.getVeilederHarLestAktivitetsplanenTopicOnPrem(), veilederHarLestDTO.getAktorId(), veilederHarLestDTO);
        store(kafkaProperties.getVeilederHarLestAkvititetsplanenTopicAiven(), veilederHarLestDTO.getAktorId(), veilederHarLestDTO);
    }


    private void store(String topic, String key, Object value) {
        ProducerRecord<byte[], byte[]> record = serializeJsonRecord(new ProducerRecord<>(topic, key, value));
        producerRecordStorage.store(record);
    }

}

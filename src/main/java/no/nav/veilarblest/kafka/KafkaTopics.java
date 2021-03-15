package no.nav.veilarblest.kafka;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KafkaTopics {

    private String veilederHarLestAktivitetsplanen;

    public static KafkaTopics create() {
        KafkaTopics kafkaTopics = new KafkaTopics();

        kafkaTopics.setVeilederHarLestAktivitetsplanen("aapen-fo-veilederHarLestAktivitetsplanen-v1");

        return kafkaTopics;
    }
}

package no.nav.veilarblest.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static java.lang.String.format;
import static no.nav.common.json.JsonUtils.toJson;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaMessagePublisher {
    private final KafkaTopics kafkaTopics;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final FeiletKafkaMeldingRepository feiletKafkaMeldingRepository;

    public void publiserVeilederHarLestAktivitetsplanen(VeilederHarLestDTO malEndringKafkaDTO) {
        publiser(kafkaTopics.getVeilederHarLestAktivitetsplanen(), malEndringKafkaDTO.getAktorId(), toJson(malEndringKafkaDTO));
    }
    private void onSuccess(String topic, String key) {
        log.info(format("Publiserte melding på topic %s med key %s", topic, key));
    }

    private void onError(String topicName, String messageKey, String jsonPayload, Throwable throwable) {
        log.error(format("Kunne ikke publisere melding på topic %s med key %s \nERROR: %s", topicName, messageKey, throwable));
        feiletKafkaMeldingRepository.lagreFeiletKafkaMelding(topicName, messageKey, jsonPayload);
    }

    public void publiserTidligereFeiletMelding(FeiletKafkaMelding feiletKafkaMelding) {
        kafkaTemplate.send(feiletKafkaMelding.getTopicName(), feiletKafkaMelding.getMessageKey(), feiletKafkaMelding.getJsonPayload())
                .addCallback(
                        sendResult -> onSuccessTidligereFeilet(feiletKafkaMelding),
                        throwable -> onErrorTidligereFeilet(feiletKafkaMelding, throwable)
                );
    }

    private void publiser(String topicName, String messageKey, String jsonPayload) {
        kafkaTemplate.send(topicName, messageKey, jsonPayload)
                .addCallback(
                        sendResult -> onSuccess(topicName, messageKey),
                        throwable -> onError(topicName, messageKey, jsonPayload, throwable)
                );
    }

    private void onSuccessTidligereFeilet(FeiletKafkaMelding feiletKafkaMelding) {
        String topicName = feiletKafkaMelding.getTopicName();
        String messageKey = feiletKafkaMelding.getMessageKey();

        log.info(format("Publiserte tidligere feilet melding på topic %s med key %s", topicName, messageKey));
        feiletKafkaMeldingRepository.slettFeiletKafkaMelding(feiletKafkaMelding.getId());
    }

    private void onErrorTidligereFeilet(FeiletKafkaMelding feiletKafkaMelding, Throwable throwable) {
        String topicName = feiletKafkaMelding.getTopicName();
        String messageKey = feiletKafkaMelding.getMessageKey();

        log.error(format("Kunne ikke publisere tidligere feilet melding på topic %s med key %s \nERROR: %s", topicName, messageKey, throwable));
    }
}

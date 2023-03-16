package no.nav.veilarblest.rest;

import io.restassured.RestAssured;
import no.nav.veilarblest.SpringBootTestBase;
import no.nav.veilarblest.rest.domain.LestDto;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

public class LestRessursITest extends SpringBootTestBase {


    KafkaConsumer<String, String> kafkaConsumer;
    private String kafkaGroupId;

    @BeforeEach
    public void setup() {
        kafkaGroupId = UUID.randomUUID().toString();
        var props = new Properties();
        props.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, kafkaBroker.getBrokersAsString());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaGroupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        kafkaConsumer = new KafkaConsumer<>(props);
        kafkaConsumer.subscribe(List.of("veileder-har-lest-aktivitetsplanen"));
    }

    @Test
    void kanari() {

        RestAssured.given()
                .port(port)
                .put("http://localhost/veilarblest/api/informasjon/les?versjon={versjon}", "versjon1")
                .then()
                .statusCode(200);
        List<LestDto> lestDtos = RestAssured.given()
                .port(port)
                .get("http://localhost/veilarblest/api/aktivitetsplan/les?fnr={fnr}", "01010101010")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .jsonPath().getList(".", LestDto.class);

        Assertions.assertThat(lestDtos).hasSize(1);
        ConsumerRecord<String, String> singleRecord = KafkaTestUtils.getSingleRecord(kafkaConsumer, "veileder-har-lest-aktivitetsplanen", Duration.ofMillis(10000));
        Assertions.assertThat(singleRecord).isNotNull();
    }
}
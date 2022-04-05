package no.nav.veilarblest.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class KafkaFeiletMeldingSchedule {
    private final static long FIVE_MINUTES = 5 * 60 * 1000;
    private final static long ONE_MINUTE = 60 * 1000;

    private final FeiletKafkaMeldingRepository feiletKafkaMeldingRepository;

    private final KafkaMessagePublisher kafkaMessagePublisher;

    @Scheduled(fixedDelay = ONE_MINUTE, initialDelay = FIVE_MINUTES)
    @SchedulerLock(name = "publiser_tidligere_feil", lockAtMostFor = "PT5M", lockAtLeastFor = "PT30S")
    public void publiserTidligereFeilet() {
        MDC.put("running.job", "publiserFeiledePaaKafka");
        log.info("publiser_tidligere_feilet");
        List<FeiletKafkaMelding> feiledeMeldinger = feiletKafkaMeldingRepository.hentFeiledeKafkaMeldinger(1000);
        feiledeMeldinger.forEach(kafkaMessagePublisher::publiserTidligereFeiletMelding);
        MDC.clear();
    }
}

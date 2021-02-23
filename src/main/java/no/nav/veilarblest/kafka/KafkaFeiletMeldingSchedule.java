package no.nav.veilarblest.kafka;

import lombok.extern.slf4j.Slf4j;
import no.nav.common.leaderelection.LeaderElectionClient;
import no.nav.common.utils.job.JobUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

@Slf4j
public class KafkaFeiletMeldingSchedule {
    private final static long FIVE_MINUTES = 5 * 60 * 1000;

    private final static long ONE_MINUTE = 60 * 1000;

    private final LeaderElectionClient leaderElectionClient;

    private final FeiletKafkaMeldingRepository feiletKafkaMeldingRepository;

    private final KafkaMessagePublisher kafkaMessagePublisher;

    @Autowired
    public KafkaFeiletMeldingSchedule(
            LeaderElectionClient leaderElectionClient,
            FeiletKafkaMeldingRepository feiletKafkaMeldingRepository,
            KafkaMessagePublisher kafkaMessagePublisher
    ) {
        this.leaderElectionClient = leaderElectionClient;
        this.feiletKafkaMeldingRepository = feiletKafkaMeldingRepository;
        this.kafkaMessagePublisher = kafkaMessagePublisher;
    }

    @Scheduled(fixedDelay = FIVE_MINUTES, initialDelay = ONE_MINUTE)
    public void publiserTidligereFeilet() {
        if (leaderElectionClient.isLeader()) {
            log.info("publiser_tidligere_feilet");
            JobUtils.runAsyncJob( () -> {
                List<FeiletKafkaMelding> feiledeMeldinger = feiletKafkaMeldingRepository.hentFeiledeKafkaMeldinger(1000);
                feiledeMeldinger.forEach(kafkaMessagePublisher::publiserTidligereFeiletMelding);
            });
        }
    }
}

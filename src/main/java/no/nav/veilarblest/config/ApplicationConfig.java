package no.nav.veilarblest.config;

import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import no.nav.common.auth.context.AuthContextHolder;
import no.nav.common.auth.context.AuthContextHolderThreadLocal;
import no.nav.common.client.aktoroppslag.AktorOppslagClient;
import no.nav.common.client.aktoroppslag.CachedAktorOppslagClient;
import no.nav.common.client.aktoroppslag.PdlAktorOppslagClient;
import no.nav.common.job.leader_election.LeaderElectionClient;
import no.nav.common.job.leader_election.ShedLockLeaderElectionClient;
import no.nav.common.token_client.builder.AzureAdTokenClientBuilder;
import no.nav.common.token_client.client.AzureAdMachineToMachineTokenClient;
import no.nav.common.token_client.client.MachineToMachineTokenClient;
import no.nav.common.utils.Credentials;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

import static no.nav.common.utils.EnvironmentUtils.isProduction;
import static no.nav.common.utils.NaisUtils.getCredentials;
import static no.nav.common.utils.UrlUtils.createServiceUrl;


@Slf4j
@Configuration
@EnableScheduling
@EnableConfigurationProperties({EnvironmentProperties.class})
public class ApplicationConfig {
    public static final String APPLICATION_NAME = "veilarblest";

    @Bean
    public AuthContextHolder authContextHolder() {
        return AuthContextHolderThreadLocal.instance();
    }

    @Bean
    public Credentials serviceUserCredentials() {
        return getCredentials("service_user");
    }

    @Bean
    public AzureAdMachineToMachineTokenClient tokenClient() {
        return AzureAdTokenClientBuilder.builder()
                .withNaisDefaults()
                .buildMachineToMachineTokenClient();
    }

    @Bean
    public AktorOppslagClient aktorregisterClient(MachineToMachineTokenClient tokenClient) {
        String tokenScop = String.format("api://%s-fss.pdl.pdl-api/.default",
                isProduction().orElse(false) ? "prod" : "dev"
        );
        return new CachedAktorOppslagClient(new PdlAktorOppslagClient(
                createServiceUrl("pdl-api", "pdl", false),
                () -> tokenClient.createMachineToMachineToken(tokenScop))
        );
    }

    @Bean
    public LockProvider lockProvider(JdbcTemplate jdbcTemplate) {
        return new JdbcTemplateLockProvider(jdbcTemplate);
    }

    @Bean
    public LeaderElectionClient leaderElectionClient(LockProvider lockProvider) {
        return new ShedLockLeaderElectionClient(lockProvider);
    }

}

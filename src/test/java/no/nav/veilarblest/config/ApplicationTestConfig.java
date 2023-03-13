package no.nav.veilarblest.config;

import no.nav.common.client.aktorregister.AktorregisterClient;
import no.nav.common.token_client.client.AzureAdMachineToMachineTokenClient;
import no.nav.common.utils.Credentials;
import no.nav.veilarblest.mock.AktorregisterClientMock;
import no.nav.veilarblest.rest.LestRessurs;
import org.mockito.Mockito;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

@Configuration
@EnableConfigurationProperties({EnvironmentProperties.class})
@Import({
        DatabaseTestConfig.class,
        LestRessurs.class,
        FilterTestConfig.class
})
public class ApplicationTestConfig {

    @Bean
    public Credentials serviceUserCredentials() {
        return new Credentials("username", "password");
    }

    @Bean
    public AktorregisterClient aktorregisterClient() {
        return new AktorregisterClientMock();
    }

    @Bean
    public AzureAdMachineToMachineTokenClient azureAdMachineToMachineTokenClient() {
        AzureAdMachineToMachineTokenClient tokenClient = mock(AzureAdMachineToMachineTokenClient.class);
        Mockito.when(tokenClient.createMachineToMachineToken(any())).thenReturn("mockMachineToMachineToken");
        return tokenClient;
    }
}

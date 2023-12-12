package no.nav.veilarblest.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.env")
public class EnvironmentProperties {

    private String azureAdDiscoveryUrl;

    private String azureAdLoginServiceClientId;

    private String azureAdClientId;

    private String loginserviceIdportenDiscoveryUrl;

    private String loginserviceIdportenAudience;

    private String dbUrl;
    
    private String environmentName;

    private String pdlScope;


}

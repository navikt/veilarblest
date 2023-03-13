package no.nav.veilarblest.config;

import no.nav.common.auth.context.UserRole;
import no.nav.common.auth.oidc.filter.AzureAdUserRoleResolver;
import no.nav.common.auth.oidc.filter.OidcAuthenticationFilter;
import no.nav.common.auth.oidc.filter.OidcAuthenticatorConfig;
import no.nav.common.log.LogFilter;
import no.nav.common.rest.filter.SetStandardHttpHeadersFilter;
import no.nav.common.token_client.utils.env.TokenXEnvironmentvariables;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

import static no.nav.common.auth.Constants.AZURE_AD_B2C_ID_TOKEN_COOKIE_NAME;
import static no.nav.common.auth.oidc.filter.OidcAuthenticator.fromConfig;
import static no.nav.common.utils.EnvironmentUtils.isDevelopment;

@Configuration
public class FilterConfig {

    @Value("${spring.application.name}")
    private String applicationName;

    private OidcAuthenticatorConfig loginserviceIdportenConfig(EnvironmentProperties environmentProperties) {
        return new OidcAuthenticatorConfig()
                .withDiscoveryUrl(environmentProperties.getLoginserviceIdportenDiscoveryUrl())
                .withClientId(environmentProperties.getLoginserviceIdportenAudience())
                .withIdTokenCookieName(AZURE_AD_B2C_ID_TOKEN_COOKIE_NAME)
                .withUserRole(UserRole.EKSTERN);
    }

    private OidcAuthenticatorConfig naisAzureAdConfig(EnvironmentProperties environmentProperties) {
        return new OidcAuthenticatorConfig()
                .withDiscoveryUrl(environmentProperties.getAzureAdDiscoveryUrl())
                .withClientId(environmentProperties.getAzureAdClientId())
                .withUserRoleResolver(new AzureAdUserRoleResolver());
    }

    private OidcAuthenticatorConfig tokenxConfig() {
        return new OidcAuthenticatorConfig()
                .withDiscoveryUrl(System.getenv(TokenXEnvironmentvariables.TOKEN_X_WELL_KNOWN_URL))
                .withClientId(System.getenv(TokenXEnvironmentvariables.TOKEN_X_CLIENT_ID))
                .withUserRole(UserRole.EKSTERN);
    }

    @Bean
    public FilterRegistrationBean pingFilter() {
        // Veilarbproxy trenger dette endepunktet for å sjekke at tjenesten lever
        // /internal kan ikke brukes siden det blir stoppet før det kommer frem

        FilterRegistrationBean<PingFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new PingFilter());
        registration.setOrder(1);
        registration.addUrlPatterns("/api/ping");
        return registration;
    }

    @Bean
    @ConditionalOnProperty(
            value = "application.oidc.disabled",
            havingValue = "false",
            matchIfMissing = true
    )
    public FilterRegistrationBean authenticationFilterRegistrationBean(EnvironmentProperties properties) {
        OidcAuthenticatorConfig azureAdB2cConfig = loginserviceIdportenConfig(properties);
        OidcAuthenticatorConfig naisAzureAdConfig = naisAzureAdConfig(properties);

        FilterRegistrationBean<OidcAuthenticationFilter> registration = new FilterRegistrationBean<>();
        OidcAuthenticationFilter authenticationFilter = new OidcAuthenticationFilter(
                Arrays.asList(
                        fromConfig(azureAdB2cConfig),
                        fromConfig(naisAzureAdConfig),
                        fromConfig(tokenxConfig())
                )
        );

        registration.setFilter(authenticationFilter);
        registration.setOrder(2);
        registration.addUrlPatterns("/api/*");
        return registration;
    }

    @Bean
    public FilterRegistrationBean logFilterRegistrationBean() {
        FilterRegistrationBean<LogFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new LogFilter(applicationName, isDevelopment().orElse(false)));
        registration.setOrder(3);
        registration.addUrlPatterns("/*");
        return registration;
    }

    @Bean
    public FilterRegistrationBean setStandardHeadersFilterRegistrationBean() {
        FilterRegistrationBean<SetStandardHttpHeadersFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new SetStandardHttpHeadersFilter());
        registration.setOrder(4);
        registration.addUrlPatterns("/*");
        return registration;
    }

}

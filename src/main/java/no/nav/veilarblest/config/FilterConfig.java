package no.nav.veilarblest.config;

import no.nav.common.auth.oidc.filter.OidcAuthenticationFilter;
import no.nav.common.auth.oidc.filter.OidcAuthenticatorConfig;
import no.nav.common.auth.subject.IdentType;
import no.nav.common.auth.utils.UserTokenFinder;
import no.nav.common.log.LogFilter;
import no.nav.common.rest.filter.SetStandardHttpHeadersFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

import static no.nav.common.auth.Constants.*;
import static no.nav.common.auth.oidc.filter.OidcAuthenticator.fromConfig;
import static no.nav.common.utils.EnvironmentUtils.isDevelopment;
import static no.nav.common.utils.EnvironmentUtils.requireApplicationName;

@Configuration
public class FilterConfig {

    public OidcAuthenticatorConfig openAmAuthConfig(EnvironmentProperties environmentProperties) {
        return new OidcAuthenticatorConfig()
                .withDiscoveryUrl(environmentProperties.getOpenAmDiscoveryUrl())
                .withClientId(environmentProperties.getOpenAmClientId())
                .withIdTokenCookieName(OPEN_AM_ID_TOKEN_COOKIE_NAME)
                .withRefreshTokenCookieName(REFRESH_TOKEN_COOKIE_NAME)
                .withRefreshUrl(environmentProperties.getOpenAmRefreshUrl())
                .withIdTokenFinder(new UserTokenFinder())
                .withIdentType(IdentType.InternBruker);
    }

    public OidcAuthenticatorConfig azureAdAuthConfig(EnvironmentProperties environmentProperties) {
        return new OidcAuthenticatorConfig()
                .withDiscoveryUrl(environmentProperties.getAzureAdDiscoveryUrl())
                .withClientId(environmentProperties.getAzureAdClientId())
                .withIdTokenCookieName(AZURE_AD_ID_TOKEN_COOKIE_NAME)
                .withIdentType(IdentType.InternBruker);
    }

    public OidcAuthenticatorConfig loginserviceIdportenConfig(EnvironmentProperties environmentProperties) {
        return new OidcAuthenticatorConfig()
                .withDiscoveryUrl(environmentProperties.getLoginserviceIdportenDiscoveryUrl())
                .withClientId(environmentProperties.getLoginserviceIdportenAudience())
                .withIdTokenCookieName(AZURE_AD_B2C_ID_TOKEN_COOKIE_NAME)
                .withIdentType(IdentType.EksternBruker);
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
    public FilterRegistrationBean authenticationFilterRegistrationBean(EnvironmentProperties properties) {
        OidcAuthenticatorConfig openAmConfig = openAmAuthConfig(properties);
        OidcAuthenticatorConfig azureAdConfig = azureAdAuthConfig(properties);
        OidcAuthenticatorConfig azureAdB2cConfig = loginserviceIdportenConfig(properties);

        FilterRegistrationBean<OidcAuthenticationFilter> registration = new FilterRegistrationBean<>();
        OidcAuthenticationFilter authenticationFilter = new OidcAuthenticationFilter(
                Arrays.asList(fromConfig(openAmConfig), fromConfig(azureAdConfig), fromConfig(azureAdB2cConfig))
        );

        registration.setFilter(authenticationFilter);
        registration.setOrder(2);
        registration.addUrlPatterns("/api/*");
        return registration;
    }

    @Bean
    public FilterRegistrationBean logFilterRegistrationBean() {
        FilterRegistrationBean<LogFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new LogFilter(requireApplicationName(), isDevelopment().orElse(false)));
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

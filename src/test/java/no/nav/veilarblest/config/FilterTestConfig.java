package no.nav.veilarblest.config;

import no.nav.common.auth.context.UserRole;
import no.nav.common.test.auth.TestAuthContextFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterTestConfig {
    public static final String TEST_IDENT = "Z123456";

    @Bean
    public FilterRegistrationBean testSubjectFilterRegistrationBean() {
        FilterRegistrationBean<TestAuthContextFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new TestAuthContextFilter(UserRole.INTERN, TEST_IDENT));
        registration.setOrder(1);
        registration.addUrlPatterns("/api/*");
        return registration;
    }

}

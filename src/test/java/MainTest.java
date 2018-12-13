import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import no.nav.testconfig.ApiAppTest;
import org.slf4j.LoggerFactory;

import static ch.qos.logback.classic.Level.DEBUG;
import static no.nav.fo.veilarblest.TestSetup.setupEnvironment;
import static no.nav.fo.veilarblest.config.ApplicationConfig.APPLICATION_NAME;
import static no.nav.testconfig.ApiAppTest.Config.builder;

public class MainTest {

    private static final String PORT = "8900";

    public static void main(String[] args) {
        ApiAppTest.setupTestContext(builder().applicationName(APPLICATION_NAME).build());

        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger logger = loggerContext.getLogger("org.jooq");
        logger.setLevel(DEBUG);

        setupEnvironment();

        Main.main(PORT);
    }

}

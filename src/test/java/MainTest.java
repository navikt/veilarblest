import no.nav.testconfig.ApiAppTest;

import static no.nav.fo.veilarblest.config.ApplicationConfig.APPLICATION_NAME;
import static no.nav.testconfig.ApiAppTest.Config.builder;

public class MainTest {

    public static final String TEST_PORT = "8800";

    public static void main(String[] args) {
        ApiAppTest.setupTestContext(builder().applicationName(APPLICATION_NAME).build());
        Main.main(TEST_PORT);
    }

}

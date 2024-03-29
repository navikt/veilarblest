package no.nav.veilarblest.rest;

import no.nav.common.health.HealthCheckResult;
import no.nav.common.health.selftest.SelfTestCheck;
import no.nav.common.health.selftest.SelfTestUtils;
import no.nav.common.health.selftest.SelftTestCheckResult;
import no.nav.common.health.selftest.SelftestHtmlGenerator;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

import static no.nav.common.health.selftest.SelfTestUtils.checkAllParallel;

@RestController
@RequestMapping("/internal")
public class InternalController {

    private final DSLContext dslContext;

    private final List<SelfTestCheck> selftestChecks;

    @Autowired
    public InternalController(DSLContext dslContext) {
        this.dslContext = dslContext;
        this.selftestChecks = Arrays.asList(
                new SelfTestCheck("Database ping", true, () -> checkDbHealth(dslContext))
        );
    }

    @GetMapping("/isReady")
    public void isReady() { }

    @GetMapping("/isAlive")
    public void isAlive() {
        if(checkDbHealth(dslContext).isUnhealthy()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

    @GetMapping("/selftest")
    public ResponseEntity selftest() {
        List<SelftTestCheckResult> checkResults = checkAllParallel(selftestChecks);
        String html = SelftestHtmlGenerator.generate(checkResults);
        int status = SelfTestUtils.findHttpStatusCode(checkResults, true);

        return ResponseEntity
                .status(status)
                .contentType(MediaType.TEXT_HTML)
                .body(html);
    }

    public static HealthCheckResult checkDbHealth(DSLContext dslContext) {
        try {
            dslContext.selectOne().fetch();
            return HealthCheckResult.healthy();
        } catch (Exception e) {
            return HealthCheckResult.unhealthy("Fikk ikke kontakt med databasen", e);
        }
    }

}

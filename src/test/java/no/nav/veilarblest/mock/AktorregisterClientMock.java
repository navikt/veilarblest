package no.nav.veilarblest.mock;

import no.nav.common.client.aktoroppslag.BrukerIdenter;
import no.nav.common.client.aktorregister.AktorregisterClient;
import no.nav.common.client.aktorregister.IngenGjeldendeIdentException;
import no.nav.common.health.HealthCheckResult;
import no.nav.common.types.identer.AktorId;
import no.nav.common.types.identer.EksternBrukerId;
import no.nav.common.types.identer.Fnr;

import java.util.List;
import java.util.Map;

public class AktorregisterClientMock implements AktorregisterClient {

    public final static String TEST_FNR = "fnr";
    public final static String TEST_AKTOR_ID = "123";
    public final static String TEST_VEILEDER_IDENT = "Z12345";

    @Override
    public HealthCheckResult checkHealth() {
        return HealthCheckResult.healthy();
    }

    @Override
    public List<AktorId> hentAktorIder(Fnr fnr) {
        return null;
    }

    @Override
    public Fnr hentFnr(AktorId aktorId) throws IngenGjeldendeIdentException {
        return Fnr.of(TEST_FNR);
    }

    @Override
    public AktorId hentAktorId(Fnr fnr) throws IngenGjeldendeIdentException {
        return AktorId.of(TEST_AKTOR_ID);
    }

    @Override
    public Map<AktorId, Fnr> hentFnrBolk(List<AktorId> list) {
        return null;
    }

    @Override
    public Map<Fnr, AktorId> hentAktorIdBolk(List<Fnr> list) {
        return null;
    }

    @Override
    public BrukerIdenter hentIdenter(EksternBrukerId eksternBrukerId) {
        return null;
    }
}

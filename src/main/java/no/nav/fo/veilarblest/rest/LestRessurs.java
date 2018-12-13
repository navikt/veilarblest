package no.nav.fo.veilarblest.rest;

import no.nav.common.auth.SubjectHandler;
import no.nav.dialogarena.aktor.AktorService;
import no.nav.fo.veilarblest.domain.tables.records.LestRecord;
import no.nav.fo.veilarblest.rest.domain.LestDto;
import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import java.time.Instant;

import static java.time.ZoneId.systemDefault;
import static no.nav.fo.veilarblest.domain.enums.Ressurs.aktivitetsplan;
import static no.nav.fo.veilarblest.domain.tables.Lest.LEST;
import static org.jooq.impl.DSL.currentLocalDateTime;

@Path("/")
@Component
public class LestRessurs {

    @Inject
    private DSLContext db;

    @Inject
    private AktorService aktorService;

    @GET
    @Path("/aktivitetsplan/les")
    public LestDto lesAktivitetsplan(@QueryParam("fnr") String fnr) {

        String brukerId = SubjectHandler.getIdent().orElseThrow(RuntimeException::new);
        String aktorId = aktorService.getAktorId(fnr).orElseThrow(RuntimeException::new);

        LestDto result = db.selectFrom(LEST)
                .where(LEST.AV.eq(brukerId))
                .and(LEST.EIER.eq(aktorId))
                .and(LEST.RESSURS.eq(aktivitetsplan))
                .orderBy(LEST.TIDSPUNKT.desc())
                .limit(1)
                .fetchOptional(this::map)
                .orElse(initialLest());

        db.insertInto(LEST)
                .set(LEST.AV, brukerId)
                .set(LEST.EIER, aktorId)
                .set(LEST.RESSURS, aktivitetsplan)
                .set(LEST.TIDSPUNKT, currentLocalDateTime())
                .execute();

        return result;
    }

    private LestDto map(LestRecord record) {
        return new LestDto()
                .setLestTidspunkt(record.get(LEST.TIDSPUNKT));
    }

    private LestDto initialLest() {
        return new LestDto()
                .setLestTidspunkt(Instant.EPOCH.atZone(systemDefault()).toLocalDateTime());
    }

}

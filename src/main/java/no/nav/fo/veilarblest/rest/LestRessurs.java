package no.nav.fo.veilarblest.rest;

import no.nav.brukerdialog.security.domain.IdentType;
import no.nav.dialogarena.aktor.AktorService;
import no.nav.fo.veilarblest.domain.enums.Ressurs;
import no.nav.fo.veilarblest.domain.tables.records.AndresRessurserRecord;
import no.nav.fo.veilarblest.domain.tables.records.MineRessurserRecord;
import no.nav.fo.veilarblest.rest.domain.LestDto;
import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static no.nav.brukerdialog.security.domain.IdentType.EksternBruker;
import static no.nav.brukerdialog.security.domain.IdentType.InternBruker;
import static no.nav.common.auth.SubjectHandler.getIdent;
import static no.nav.common.auth.SubjectHandler.getIdentType;
import static no.nav.fo.veilarblest.domain.enums.Ressurs.aktivitetsplan;
import static no.nav.fo.veilarblest.domain.enums.Ressurs.informasjon;
import static no.nav.fo.veilarblest.domain.tables.AndresRessurser.ANDRES_RESSURSER;
import static no.nav.fo.veilarblest.domain.tables.MineRessurser.MINE_RESSURSER;
import static org.jooq.impl.DSL.currentLocalDateTime;

@Path("/")
@Component
public class LestRessurs {

    private final DSLContext db;

    private final AktorService aktorService;

    @Inject
    public LestRessurs(DSLContext db, AktorService aktorService) {
        this.db = db;
        this.aktorService = aktorService;
    }

    @GET
    @Path("/aktivitetsplan/les")
    public List<LestDto> lesAktivitetsplan(@QueryParam("fnr") String fnr) {
        String currentUser = getBrukerId();
        List<LestDto> mineRessurser = getMineLestRessurser(currentUser);

        IdentType identType = getIdentType().orElseThrow(RuntimeException::new);
        if (EksternBruker.equals(identType)) {
            insertMinLestRessurs(currentUser, aktivitetsplan);
        } else if (InternBruker.equals(identType)) {
            String eier = aktorService.getAktorId(fnr).orElseThrow(RuntimeException::new);
            List<LestDto> andresLestRessurser = getAndresLestRessurser(eier, currentUser);
            insertAndresLestRessurs(eier, currentUser, aktivitetsplan);
            mineRessurser.addAll(andresLestRessurser);
        }

        return mineRessurser;
    }


    private void insertMinLestRessurs(String eier, Ressurs ressurs) {
        db.mergeInto(MINE_RESSURSER, MINE_RESSURSER.EIER, MINE_RESSURSER.RESSURS, MINE_RESSURSER.TIDSPUNKT)
                .key(MINE_RESSURSER.EIER, MINE_RESSURSER.RESSURS)
                .values(eier, ressurs, LocalDateTime.now())
                .execute();
    }

    private void insertAndresLestRessurs(String eier, String lestAv, Ressurs ressurs) {
        db.mergeInto(
                ANDRES_RESSURSER,
                ANDRES_RESSURSER.EIER,
                ANDRES_RESSURSER.LEST_AV,
                ANDRES_RESSURSER.RESSURS,
                ANDRES_RESSURSER.TIDSPUNKT
        )
                .key(ANDRES_RESSURSER.EIER, ANDRES_RESSURSER.LEST_AV, ANDRES_RESSURSER.RESSURS)
                .values(eier, lestAv, ressurs, LocalDateTime.now())
                .execute();
    }

    private String getBrukerId() {
        IdentType identType = getIdentType().orElseThrow(RuntimeException::new);
        String brukerId = getIdent().orElseThrow(RuntimeException::new);
        if (EksternBruker.equals(identType)) {
            return aktorService.getAktorId(brukerId).orElseThrow(RuntimeException::new);
        }
        return brukerId;
    }

    private List<LestDto> getMineLestRessurser(String eier) {
        return db.selectFrom(MINE_RESSURSER)
                .where(MINE_RESSURSER.EIER.eq(eier))
                .fetch()
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    private List<LestDto> getAndresLestRessurser(String eier, String lestAv) {
        return db.selectFrom(ANDRES_RESSURSER)
                .where(ANDRES_RESSURSER.EIER.eq(eier))
                .and(ANDRES_RESSURSER.LEST_AV.eq(lestAv))
                .fetch()
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    @PUT
    @Path("/informasjon/les")
    public void lesInformasjon(@QueryParam("versjon") String versjon) {
        String brukerId = getBrukerId();
        db.mergeInto(MINE_RESSURSER,
                MINE_RESSURSER.EIER,
                MINE_RESSURSER.RESSURS,
                MINE_RESSURSER.TIDSPUNKT,
                MINE_RESSURSER.VERDI
        )
                .key(MINE_RESSURSER.EIER, MINE_RESSURSER.RESSURS)
                .values(brukerId, informasjon, LocalDateTime.now(), versjon)
                .execute();
    }

    private LestDto map(MineRessurserRecord record) {
        return new LestDto()
                .setTidspunkt(record.get(MINE_RESSURSER.TIDSPUNKT))
                .setVerdi(record.get(MINE_RESSURSER.VERDI))
                .setRessurs(record.get(MINE_RESSURSER.RESSURS).getLiteral());
    }

    private LestDto map(AndresRessurserRecord record) {
        return new LestDto()
                .setTidspunkt(record.get(ANDRES_RESSURSER.TIDSPUNKT))
                .setRessurs(record.get(ANDRES_RESSURSER.RESSURS).getLiteral());
    }

}

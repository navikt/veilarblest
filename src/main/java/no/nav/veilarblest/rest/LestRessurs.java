package no.nav.veilarblest.rest;

import lombok.extern.slf4j.Slf4j;
import no.nav.common.auth.subject.IdentType;
import no.nav.common.auth.subject.SubjectHandler;
import no.nav.common.client.aktorregister.AktorregisterClient;
import no.nav.veilarblest.domain.enums.Ressurs;
import no.nav.veilarblest.domain.tables.records.AndresRessurserRecord;
import no.nav.veilarblest.domain.tables.records.MineRessurserRecord;
import no.nav.veilarblest.rest.domain.LestDto;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.QueryParam;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static no.nav.common.auth.subject.IdentType.EksternBruker;
import static no.nav.common.auth.subject.IdentType.InternBruker;
import static no.nav.veilarblest.domain.enums.Ressurs.aktivitetsplan;
import static no.nav.veilarblest.domain.enums.Ressurs.informasjon;
import static no.nav.veilarblest.domain.tables.AndresRessurser.ANDRES_RESSURSER;
import static no.nav.veilarblest.domain.tables.MineRessurser.MINE_RESSURSER;

@Slf4j
@RestController
@RequestMapping("/api")
public class LestRessurs {

    private final DSLContext db;

    private final AktorregisterClient aktorregisterClient;

    @Autowired
    public LestRessurs(DSLContext db, AktorregisterClient aktorregisterClient) {
        this.db = db;
        this.aktorregisterClient = aktorregisterClient;
    }

    @GetMapping("/aktivitetsplan/les")
    public List<LestDto> lesAktivitetsplan(@QueryParam("fnr") String fnr) {
        String currentUser = getBrukerId();
        List<LestDto> mineRessurser = getMineLestRessurser(currentUser);
        IdentType identType = SubjectHandler.getIdentType().orElseThrow(RuntimeException::new);
        if (EksternBruker.equals(identType)) {
            insertMinLestRessurs(currentUser, aktivitetsplan);
        } else if (InternBruker.equals(identType)) {
            String eier = aktorregisterClient.hentAktorId(fnr);
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
        IdentType identType = SubjectHandler.getIdentType().orElseThrow(RuntimeException::new);
        String brukerId = SubjectHandler.getIdent().orElseThrow(RuntimeException::new);
        if (EksternBruker.equals(identType)) {
            return aktorregisterClient.hentAktorId(brukerId);
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

    @PutMapping("/informasjon/les")
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

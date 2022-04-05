package no.nav.veilarblest.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.common.auth.context.AuthContextHolder;
import no.nav.common.client.aktoroppslag.AktorOppslagClient;
import no.nav.common.types.identer.Fnr;
import no.nav.veilarblest.domain.enums.Ressurs;
import no.nav.veilarblest.domain.tables.records.AndresRessurserRecord;
import no.nav.veilarblest.domain.tables.records.MineRessurserRecord;
import no.nav.veilarblest.kafka.KafkaMessagePublisher;
import no.nav.veilarblest.kafka.VeilederHarLestDTO;
import no.nav.veilarblest.rest.domain.LestDto;
import org.jooq.DSLContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.ws.rs.QueryParam;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static no.nav.veilarblest.domain.enums.Ressurs.aktivitetsplan;
import static no.nav.veilarblest.domain.enums.Ressurs.informasjon;
import static no.nav.veilarblest.domain.tables.AndresRessurser.ANDRES_RESSURSER;
import static no.nav.veilarblest.domain.tables.MineRessurser.MINE_RESSURSER;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LestRessurs {

    private final DSLContext db;
    private final AktorOppslagClient aktorOppslagClient;
    private final KafkaMessagePublisher messagePublisher;
    private final AuthContextHolder authContextHolder;

    @GetMapping("/aktivitetsplan/les")
    public List<LestDto> lesAktivitetsplan(@QueryParam("fnr") String fnr) {
        if (authContextHolder.erSystemBruker()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        String currentUser = getBrukerId();
        List<LestDto> mineRessurser = getMineLestRessurser(currentUser);
        ZonedDateTime lestTidspunkt = ZonedDateTime.now();

        if (authContextHolder.erEksternBruker()) {
            insertMinLestRessurs(currentUser, aktivitetsplan, lestTidspunkt.toLocalDateTime());
        } else if (authContextHolder.erInternBruker()) {
            String eier = aktorOppslagClient.hentAktorId(Fnr.of(fnr)).toString();
            List<LestDto> andresLestRessurser = getAndresLestRessurser(eier, currentUser);
            insertAndresLestRessurs(eier, currentUser, aktivitetsplan, lestTidspunkt.toLocalDateTime());
            mineRessurser.addAll(andresLestRessurser);

            messagePublisher.publiserVeilederHarLestAktivitetsplanen(
                    new VeilederHarLestDTO()
                            .setAktorId(eier)
                            .setVeilederId(currentUser)
                            .setHarLestTidspunkt(lestTidspunkt)
            );
        }

        return mineRessurser;
    }

    private void insertMinLestRessurs(String eier, Ressurs ressurs, LocalDateTime lestTidspunkt) {
        db.mergeInto(MINE_RESSURSER, MINE_RESSURSER.EIER, MINE_RESSURSER.RESSURS, MINE_RESSURSER.TIDSPUNKT)
                .key(MINE_RESSURSER.EIER, MINE_RESSURSER.RESSURS)
                .values(eier, ressurs, lestTidspunkt)
                .execute();
    }

    private void insertAndresLestRessurs(String eier, String lestAv, Ressurs ressurs, LocalDateTime lestTidspunkt) {
        db.mergeInto(
                ANDRES_RESSURSER,
                ANDRES_RESSURSER.EIER,
                ANDRES_RESSURSER.LEST_AV,
                ANDRES_RESSURSER.RESSURS,
                ANDRES_RESSURSER.TIDSPUNKT
        )
                .key(ANDRES_RESSURSER.EIER, ANDRES_RESSURSER.LEST_AV, ANDRES_RESSURSER.RESSURS)
                .values(eier, lestAv, ressurs, lestTidspunkt)
                .execute();
    }

    private String getBrukerId() {
        if (authContextHolder.erEksternBruker()) {
            return authContextHolder.getUid()
                    .map(Fnr::of)
                    .map(aktorOppslagClient::hentAktorId)
                    .orElseThrow().get();
        }
        return authContextHolder.getUid()
                .orElseThrow();

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

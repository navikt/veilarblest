package no.nav.veilarblest.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.common.auth.context.AuthContextHolder;
import no.nav.common.client.aktoroppslag.AktorOppslagClient;
import no.nav.common.types.identer.Fnr;
import no.nav.veilarblest.domain.enums.Ressurs;
import no.nav.veilarblest.domain.tables.records.AndresRessurserRecord;
import no.nav.veilarblest.domain.tables.records.MineRessurserRecord;
import no.nav.veilarblest.kafka.KafkaProducerService;
import no.nav.veilarblest.kafka.VeilederHarLestDTO;
import no.nav.veilarblest.rest.domain.FnrDto;
import no.nav.veilarblest.rest.domain.LestDto;
import org.jooq.DSLContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.ws.rs.QueryParam;
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
    private final KafkaProducerService kafkaProducerService;
    private final AuthContextHolder authContextHolder;

    @PostMapping
    public List<LestDto> lesAktivitetsplanPost(@RequestBody(required = false) FnrDto fnrDto) {
        return lesAktivitetsplanIntern(fnrDto.getFnr());
    }

    @GetMapping("/aktivitetsplan/les")
    public List<LestDto> lesAktivitetsplan(@QueryParam("fnr") String fnr) {
        return lesAktivitetsplanIntern(fnr);
    }

    public List<LestDto> lesAktivitetsplanIntern(String fnr) {
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

            kafkaProducerService.publiserVeilederHarLestAktivitetPlanen(
                    new VeilederHarLestDTO()
                            .setAktorId(eier)
                            .setVeilederId(currentUser)
                            .setHarLestTidspunkt(lestTidspunkt)
            );
        }

        return mineRessurser;
    }

    private void insertMinLestRessurs(String eier, Ressurs ressurs, LocalDateTime lestTidspunkt) {

        MineRessurserRecord nyRecord = new MineRessurserRecord();
        nyRecord.setEier(eier);
        nyRecord.setRessurs(ressurs);
        nyRecord.setTidspunkt(lestTidspunkt);

        var upsert = db.insertInto(MINE_RESSURSER)
                .set(nyRecord)
                .onConflict(MINE_RESSURSER.EIER, MINE_RESSURSER.RESSURS)
                .doUpdate()
                .set(MINE_RESSURSER.TIDSPUNKT, nyRecord.getTidspunkt());
        upsert.execute();
    }

    private void insertAndresLestRessurs(String eier, String lestAv, Ressurs ressurs, LocalDateTime lestTidspunkt) {
        AndresRessurserRecord nyRecord = db.newRecord(ANDRES_RESSURSER);
        nyRecord.setEier(eier);
        nyRecord.setLestAv(lestAv);
        nyRecord.setRessurs(ressurs);
        nyRecord.setTidspunkt(lestTidspunkt);

        var upsert = db.insertInto(ANDRES_RESSURSER)
                .set(nyRecord)
                .onConflict(ANDRES_RESSURSER.EIER, ANDRES_RESSURSER.LEST_AV, ANDRES_RESSURSER.RESSURS)
                .doUpdate()
                .set(MINE_RESSURSER.TIDSPUNKT, nyRecord.getTidspunkt());

        upsert.execute();
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
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void lesInformasjon(@QueryParam("versjon") String versjon) {
        String brukerId = getBrukerId();

        MineRessurserRecord nyRecord = db.newRecord(MINE_RESSURSER);
        nyRecord.setEier(brukerId);
        nyRecord.setRessurs(informasjon);
        nyRecord.setTidspunkt(LocalDateTime.now());
        nyRecord.setVerdi(versjon);

        var upsert = db.insertInto(MINE_RESSURSER)
                .set(nyRecord)
                .onConflict(MINE_RESSURSER.EIER, MINE_RESSURSER.RESSURS)
                .doUpdate()
                .set(MINE_RESSURSER.TIDSPUNKT, nyRecord.getTidspunkt())
                .set(MINE_RESSURSER.VERDI, nyRecord.getVerdi());
        upsert.execute();
    }

    private LestDto map(MineRessurserRecord mineRessurserRecord) {
        return new LestDto()
                .setTidspunkt(mineRessurserRecord.get(MINE_RESSURSER.TIDSPUNKT))
                .setVerdi(mineRessurserRecord.get(MINE_RESSURSER.VERDI))
                .setRessurs(mineRessurserRecord.get(MINE_RESSURSER.RESSURS).getLiteral());
    }

    private LestDto map(AndresRessurserRecord andresRessurserRecord) {
        return new LestDto()
                .setTidspunkt(andresRessurserRecord.get(ANDRES_RESSURSER.TIDSPUNKT))
                .setRessurs(andresRessurserRecord.get(ANDRES_RESSURSER.RESSURS).getLiteral());
    }

}

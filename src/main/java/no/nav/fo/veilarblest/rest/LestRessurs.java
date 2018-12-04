package no.nav.fo.veilarblest.rest;

import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

@Path("/")
@Component
public class LestRessurs {

    @GET
    @Path("/aktivitetsplan/les")
    public String lesAktivitetsplan(@QueryParam("fnr") String fnr) {
        return "Lest " + fnr;
    }

}

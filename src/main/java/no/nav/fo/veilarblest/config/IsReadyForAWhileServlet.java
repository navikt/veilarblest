package no.nav.fo.veilarblest.config;

import org.springframework.context.ApplicationContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static java.lang.System.currentTimeMillis;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static javax.servlet.http.HttpServletResponse.SC_SERVICE_UNAVAILABLE;
import static no.nav.apiapp.ServletUtil.getContext;

/*
 * TODO
 * TODO
 * TODO
 * TODO
 * TODO
 * TODO
 * TODO
 * TODO
 * TODO: Slett denne når Aura fikser at VAULT_TOKEN er gyldig i mer enn 1 time.
 */
public class IsReadyForAWhileServlet extends HttpServlet {

    public static final long FIFTY_MINUTES = 50 * 60 * 1000;
    private volatile ApplicationContext applicationContext;

    @Override
    public void init() throws ServletException {
        applicationContext = getContext(getServletContext());
        super.init();
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        boolean ready = currentTimeMillis() < (applicationContext.getStartupDate() + FIFTY_MINUTES);
        resp.setStatus(ready ? SC_OK : SC_SERVICE_UNAVAILABLE);
        resp.setContentType("text/plain");
        resp.getWriter().write(ready ? "READY" : "NOT READY");
    }

}

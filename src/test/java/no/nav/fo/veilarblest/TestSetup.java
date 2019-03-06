package no.nav.fo.veilarblest;

import no.nav.dialogarena.config.fasit.FasitUtils;
import no.nav.dialogarena.config.fasit.ServiceUser;
import no.nav.fo.veilarblest.vault.AuthenticationDTO;
import no.nav.sbl.rest.RestUtils;

import javax.ws.rs.client.Entity;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static no.nav.brukerdialog.security.Constants.*;
import static no.nav.brukerdialog.security.oidc.provider.AzureADB2CConfig.AZUREAD_B2C_DISCOVERY_URL_PROPERTY_NAME;
import static no.nav.dialogarena.config.fasit.FasitUtils.*;
import static no.nav.dialogarena.config.fasit.FasitUtils.Zone.FSS;
import static no.nav.fo.veilarblest.config.ApplicationConfig.*;
import static no.nav.sbl.dialogarena.common.cxf.StsSecurityConstants.*;
import static no.nav.sbl.rest.RestUtils.LONG_READ_CONFIG;
import static no.nav.sbl.util.EnvironmentUtils.Type.PUBLIC;
import static no.nav.sbl.util.EnvironmentUtils.Type.SECRET;
import static no.nav.sbl.util.EnvironmentUtils.setProperty;
import static no.nav.vault.jdbc.hikaricp.VaultUtil.VAULT_TOKEN_PROPERTY;

public class TestSetup {

    private static final String SERVICE_USER_ALIAS = "srvveilarblest";
    private static final String VEILARBLOGIN_REDIRECT_URL_ALIAS = "veilarblogin.redirect-url";
    private static final String SECURITY_TOKEN_SERVICE_ALIAS = "securityTokenService";
    private static final String AKTOER_V2_ALIAS = "Aktoer_v2";
    private static final String AAD_B2C_CLIENTID_ALIAS = "aad_b2c_clientid";
    private static final String AZURE_AD_B2C_DISCOVERY_ALIAS = "aad_b2c_discovery";

    public static void setupEnvironment() {
        ServiceUser serviceUser = getServiceUser(SERVICE_USER_ALIAS, APPLICATION_NAME);
        setProperty(SYSTEMUSER_USERNAME, serviceUser.getUsername(), PUBLIC);
        setProperty(SYSTEMUSER_PASSWORD, serviceUser.getPassword(), PUBLIC);

        setProperty(STS_URL_KEY, getBaseUrl(SECURITY_TOKEN_SERVICE_ALIAS, FSS), PUBLIC);
        setProperty(AKTOER_V2_URL_PROPERTY, getWebServiceEndpoint(AKTOER_V2_ALIAS).getUrl(), PUBLIC);

        ServiceUser isso_rp_user = getServiceUser("isso-rp-user", APPLICATION_NAME);
        String loginUrl = getRestService(VEILARBLOGIN_REDIRECT_URL_ALIAS).getUrl();

        setProperty(ISSO_HOST_URL_PROPERTY_NAME, getBaseUrl("isso-host"), PUBLIC);
        setProperty(ISSO_RP_USER_USERNAME_PROPERTY_NAME, isso_rp_user.getUsername(), PUBLIC);
        setProperty(ISSO_RP_USER_PASSWORD_PROPERTY_NAME, isso_rp_user.getPassword(), PUBLIC);
        setProperty(ISSO_JWKS_URL_PROPERTY_NAME, getBaseUrl("isso-jwks"), PUBLIC);
        setProperty(ISSO_ISSUER_URL_PROPERTY_NAME, getBaseUrl("isso-issuer"), PUBLIC);
        setProperty(ISSO_ISALIVE_URL_PROPERTY_NAME, getBaseUrl("isso.isalive", Zone.FSS), PUBLIC);
        setProperty(VEILARBLOGIN_REDIRECT_URL_URL_PROPERTY, loginUrl, PUBLIC);

        ServiceUser aadB2cUser = getServiceUser(AAD_B2C_CLIENTID_ALIAS, APPLICATION_NAME);
        setProperty(AZUREAD_B2C_DISCOVERY_URL_PROPERTY_NAME, getBaseUrl(AZURE_AD_B2C_DISCOVERY_ALIAS), PUBLIC);
        setProperty(AAD_B2C_CLIENTID_USERNAME_PROPERTY, aadB2cUser.getUsername(), PUBLIC);
        setProperty(AAD_B2C_CLIENTID_PASSWORD_PROPERTY, aadB2cUser.getPassword(), PUBLIC);

        Properties applicationProperties = getApplicationProperties("veilarblest.properties");
        System.getProperties().putAll(applicationProperties);

        setProperty(VAULT_TOKEN_PROPERTY, resolveVaultToken(), SECRET);
    }

    private static String resolveVaultToken() {
        return RestUtils.withClient(LONG_READ_CONFIG, c -> {

            Map<String, String> data = new HashMap<String, String>() {{
                put("password", FasitUtils.getFasitPassword());
            }};

            AuthenticationDTO authenticationDTO = c.target("https://vault.adeo.no/v1/auth/ldap/login")
                    .path(getFasitUser())
                    .request()
                    .post(Entity.json(data), AuthenticationDTO.class);
            AuthenticationDTO.Auth auth = authenticationDTO.auth;
            return auth.clientToken;
        });
    }

}

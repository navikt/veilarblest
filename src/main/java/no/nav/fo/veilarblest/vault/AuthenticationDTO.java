package no.nav.fo.veilarblest.vault;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthenticationDTO {
    public Auth auth;

    public static class Auth {
        @JsonProperty("client_token")
        public String clientToken;
    }
}

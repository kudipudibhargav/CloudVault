package com.cloudvault.auth.service;

import com.cloudvault.common.exception.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class GoogleOAuthService {

    private static final Logger log = LoggerFactory.getLogger(GoogleOAuthService.class);
    private final RestTemplate restTemplate = new RestTemplate();

    public static class GoogleUserPayload {
        private String email;
        private String firstName;
        private String lastName;
        private String googleId;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getGoogleId() {
            return googleId;
        }

        public void setGoogleId(String googleId) {
            this.googleId = googleId;
        }
    }

    public GoogleUserPayload verifyToken(String idToken) {
        String url = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken;
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                
                String email = (String) body.get("email");
                String givenName = (String) body.get("given_name");
                String familyName = (String) body.get("family_name");
                String sub = (String) body.get("sub");

                if (email == null) {
                    throw new UnauthorizedException("Google token does not contain email address");
                }

                GoogleUserPayload payload = new GoogleUserPayload();
                payload.setEmail(email);
                payload.setFirstName(givenName != null ? givenName : "");
                payload.setLastName(familyName != null ? familyName : "");
                payload.setGoogleId(sub);

                return payload;
            } else {
                throw new UnauthorizedException("Failed to verify Google ID token");
            }
        } catch (Exception e) {
            log.error("Google Token Verification Failed: ", e);
            throw new UnauthorizedException("Invalid Google ID token");
        }
    }
}

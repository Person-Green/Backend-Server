package bssm.plantshuman.peopleandgreen.auth.adapter.out.google;

import bssm.plantshuman.peopleandgreen.auth.application.config.GoogleOAuthProperties;
import bssm.plantshuman.peopleandgreen.auth.application.port.out.ExchangeGoogleAuthCodePort;
import bssm.plantshuman.peopleandgreen.auth.domain.model.GoogleUserInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;

@Component
public class GoogleOAuthClient implements ExchangeGoogleAuthCodePort {

    private final GoogleOAuthProperties properties;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public GoogleOAuthClient(
            GoogleOAuthProperties properties,
            RestClient.Builder restClientBuilder,
            ObjectMapper objectMapper
    ) {
        this.properties = properties;
        this.restClient = restClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    @Override
    public GoogleUserInfo exchange(String authorizationCode, String redirectUri) {
        LinkedMultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("code", authorizationCode);
        form.add("client_id", properties.clientId());
        form.add("client_secret", properties.clientSecret());
        form.add("redirect_uri", redirectUri);
        form.add("grant_type", "authorization_code");

        GoogleTokenResponse response = restClient.post()
                .uri(properties.tokenUri())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(GoogleTokenResponse.class);

        if (response == null || response.idToken() == null || response.idToken().isBlank()) {
            throw new IllegalArgumentException("Google id token is missing");
        }

        Map<String, Object> claims = decodeClaims(response.idToken());
        validateClaims(claims);
        return new GoogleUserInfo(
                String.valueOf(claims.get("sub")),
                String.valueOf(claims.get("email")),
                String.valueOf(claims.getOrDefault("name", "")),
                String.valueOf(claims.getOrDefault("picture", ""))
        );
    }

    private Map<String, Object> decodeClaims(String idToken) {
        try {
            String[] parts = idToken.split("\\.");
            if (parts.length < 2) {
                throw new IllegalArgumentException("Invalid google id token");
            }
            byte[] payload = Base64.getUrlDecoder().decode(parts[1]);
            return objectMapper.readValue(payload, new TypeReference<>() {
            });
        } catch (Exception exception) {
            throw new IllegalArgumentException("Failed to decode google id token", exception);
        }
    }

    private void validateClaims(Map<String, Object> claims) {
        String issuer = String.valueOf(claims.get("iss"));
        if (!"https://accounts.google.com".equals(issuer) && !"accounts.google.com".equals(issuer)) {
            throw new IllegalArgumentException("Invalid google issuer");
        }

        String audience = String.valueOf(claims.get("aud"));
        if (!properties.clientId().equals(audience)) {
            throw new IllegalArgumentException("Invalid google audience");
        }

        long expiresAt = Long.parseLong(String.valueOf(claims.get("exp")));
        if (Instant.ofEpochSecond(expiresAt).isBefore(Instant.now())) {
            throw new IllegalArgumentException("Google id token is expired");
        }
    }

    private record GoogleTokenResponse(
            @JsonProperty("access_token") String accessToken,
            @JsonProperty("id_token") String idToken
    ) {
    }
}

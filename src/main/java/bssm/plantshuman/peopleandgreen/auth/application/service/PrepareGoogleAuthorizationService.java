package bssm.plantshuman.peopleandgreen.auth.application.service;

import bssm.plantshuman.peopleandgreen.auth.application.config.GoogleOAuthProperties;
import bssm.plantshuman.peopleandgreen.auth.application.port.in.PrepareGoogleAuthorizationUseCase;
import bssm.plantshuman.peopleandgreen.auth.application.port.out.IssueJwtPort;
import bssm.plantshuman.peopleandgreen.auth.domain.model.PreparedGoogleAuthorization;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.StringJoiner;

@Service
@RequiredArgsConstructor
public class PrepareGoogleAuthorizationService implements PrepareGoogleAuthorizationUseCase {

    private final GoogleOAuthProperties properties;
    private final IssueJwtPort issueJwtPort;

    @Override
    public PreparedGoogleAuthorization prepare(String redirectUri) {
        validateRedirectUri(redirectUri);
        String state = issueJwtPort.issueGoogleState(redirectUri);
        String authorizationUrl = properties.authorizationUri()
                + "?client_id=" + encode(properties.clientId())
                + "&redirect_uri=" + encode(redirectUri)
                + "&response_type=code"
                + "&scope=" + encode(String.join(" ", properties.scope()))
                + "&state=" + encode(state)
                + "&access_type=offline"
                + "&prompt=consent";
        return new PreparedGoogleAuthorization(authorizationUrl, state);
    }

    static void validateRedirectUri(String redirectUri, GoogleOAuthProperties properties) {
        if (!properties.allowedRedirectUris().contains(redirectUri)) {
            throw new IllegalArgumentException("Redirect URI is not allowed");
        }
    }

    private void validateRedirectUri(String redirectUri) {
        validateRedirectUri(redirectUri, properties);
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}

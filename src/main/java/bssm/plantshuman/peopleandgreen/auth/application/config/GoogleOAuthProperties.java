package bssm.plantshuman.peopleandgreen.auth.application.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "auth.google")
public record GoogleOAuthProperties(
        String clientId,
        String clientSecret,
        String tokenUri,
        String authorizationUri,
        List<String> scope,
        List<String> allowedRedirectUris
) {
}

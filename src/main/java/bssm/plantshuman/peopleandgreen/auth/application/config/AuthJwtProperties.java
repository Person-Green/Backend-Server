package bssm.plantshuman.peopleandgreen.auth.application.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "auth.jwt")
public record AuthJwtProperties(
        String issuer,
        String secret,
        long accessTokenValiditySeconds,
        long refreshTokenValiditySeconds,
        long stateTokenValiditySeconds
) {
}

package bssm.plantshuman.peopleandgreen.auth.adapter.out.security;

import bssm.plantshuman.peopleandgreen.auth.application.config.AuthJwtProperties;
import bssm.plantshuman.peopleandgreen.auth.application.port.out.IssueJwtPort;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtTokenProvider implements IssueJwtPort {

    private static final String TOKEN_TYPE = "tokenType";
    private static final String REDIRECT_URI = "redirectUri";
    private static final String STATE_NONCE = "stateNonce";
    private static final String ACCESS = "ACCESS";
    private static final String REFRESH = "REFRESH";
    private static final String GOOGLE_STATE = "GOOGLE_STATE";

    private final AuthJwtProperties properties;
    private final SecretKey secretKey;

    public JwtTokenProvider(AuthJwtProperties properties) {
        this.properties = properties;
        if (properties.secret() == null || properties.secret().isBlank()) {
            throw new IllegalStateException("AUTH_JWT_SECRET must be configured");
        }
        if (properties.secret().length() < 32) {
            throw new IllegalStateException("AUTH_JWT_SECRET must be at least 32 characters");
        }
        this.secretKey = Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String issueAccessToken(Long userId) {
        return issueToken(userId, ACCESS, properties.accessTokenValiditySeconds());
    }

    @Override
    public String issueRefreshToken(Long userId) {
        return issueToken(userId, REFRESH, properties.refreshTokenValiditySeconds());
    }

    @Override
    public long getAccessTokenValiditySeconds() {
        return properties.accessTokenValiditySeconds();
    }

    @Override
    public long getRefreshTokenValiditySeconds() {
        return properties.refreshTokenValiditySeconds();
    }

    @Override
    public String issueGoogleState(String redirectUri, String stateNonce) {
        Instant now = Instant.now();
        return Jwts.builder()
                .issuer(properties.issuer())
                .subject("google-oauth-state")
                .claim(TOKEN_TYPE, GOOGLE_STATE)
                .claim(REDIRECT_URI, redirectUri)
                .claim(STATE_NONCE, stateNonce)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(properties.stateTokenValiditySeconds())))
                .signWith(secretKey)
                .compact();
    }

    @Override
    public Long parseUserId(String token) {
        Claims claims = parseClaims(token);
        return Long.parseLong(claims.getSubject());
    }

    @Override
    public boolean isRefreshToken(String token) {
        return REFRESH.equals(parseClaims(token).get(TOKEN_TYPE, String.class));
    }

    @Override
    public void validateGoogleState(String token, String redirectUri, String stateNonce) {
        try {
            Claims claims = parseClaims(token);
            String tokenType = claims.get(TOKEN_TYPE, String.class);
            if (!GOOGLE_STATE.equals(tokenType)) {
                throw new IllegalArgumentException("Invalid google oauth state type");
            }

            String expectedRedirectUri = claims.get(REDIRECT_URI, String.class);
            if (!redirectUri.equals(expectedRedirectUri)) {
                throw new IllegalArgumentException("Redirect URI does not match issued state");
            }

            String expectedStateNonce = claims.get(STATE_NONCE, String.class);
            if (!stateNonce.equals(expectedStateNonce)) {
                throw new IllegalArgumentException("OAuth state nonce does not match");
            }
        } catch (JwtException exception) {
            throw new IllegalArgumentException("Invalid google oauth state", exception);
        }
    }

    private String issueToken(Long userId, String tokenType, long validitySeconds) {
        Instant now = Instant.now();
        return Jwts.builder()
                .issuer(properties.issuer())
                .subject(String.valueOf(userId))
                .claim(TOKEN_TYPE, tokenType)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(validitySeconds)))
                .signWith(secretKey)
                .compact();
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .requireIssuer(properties.issuer())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

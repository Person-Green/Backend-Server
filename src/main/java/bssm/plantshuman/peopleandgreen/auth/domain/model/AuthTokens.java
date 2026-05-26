package bssm.plantshuman.peopleandgreen.auth.domain.model;

public record AuthTokens(
        String accessToken,
        String refreshToken,
        long expiresIn,
        long refreshExpiresIn,
        boolean firstLogin,
        AppUser user
) {
}

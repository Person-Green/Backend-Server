package bssm.plantshuman.peopleandgreen.auth.domain.model;

public record AppUser(
        Long id,
        OAuthProvider oauthProvider,
        String oauthProviderUserId,
        String email,
        String name,
        String profileImageUrl
) {
}

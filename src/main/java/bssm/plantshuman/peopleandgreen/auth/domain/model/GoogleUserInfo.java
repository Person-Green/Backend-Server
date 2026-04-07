package bssm.plantshuman.peopleandgreen.auth.domain.model;

public record GoogleUserInfo(
        String providerUserId,
        String email,
        String name,
        String profileImageUrl
) {
}

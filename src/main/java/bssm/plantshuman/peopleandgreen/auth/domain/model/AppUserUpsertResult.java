package bssm.plantshuman.peopleandgreen.auth.domain.model;

public record AppUserUpsertResult(
        AppUser user,
        boolean created
) {
}

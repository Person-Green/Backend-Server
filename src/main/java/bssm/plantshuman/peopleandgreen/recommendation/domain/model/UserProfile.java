package bssm.plantshuman.peopleandgreen.recommendation.domain.model;

public record UserProfile(
        UserEnvironment environment,
        CareLevel careLevel,
        ExperienceLevel experienceLevel,
        boolean hasPet,
        PlacementType placement
) {
}

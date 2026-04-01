package bssm.plantshuman.peopleandgreen.recommendation.domain.model;

import java.util.List;

public record PlantRecommendation(
        String plantId,
        String plantName,
        String plantEnglishName,
        int score,
        List<String> reasons,
        List<String> cautions,
        EnvironmentType representativeEnvironment,
        List<EnvironmentType> secondaryEnvironmentTags,
        AirPurificationLevel airPurificationLevel,
        PetSafetyLevel petSafetyLevel,
        DifficultyLevel difficultyLevel,
        SizeCategory sizeCategory,
        List<PlacementType> recommendedPlacements,
        String description
) {
}

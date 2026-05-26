package bssm.plantshuman.peopleandgreen.recommendation.domain.model;

import java.util.List;

public record PlantRecommendation(
        String plantId,
        String plantName,
        String plantEnglishName,
        String imageUrl,
        boolean isFavorite,
        long favoriteCount,
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
    public PlantRecommendation withFavoriteMetadata(boolean isFavorite, long favoriteCount) {
        return new PlantRecommendation(
                plantId,
                plantName,
                plantEnglishName,
                imageUrl,
                isFavorite,
                favoriteCount,
                score,
                reasons,
                cautions,
                representativeEnvironment,
                secondaryEnvironmentTags,
                airPurificationLevel,
                petSafetyLevel,
                difficultyLevel,
                sizeCategory,
                recommendedPlacements,
                description
        );
    }
}

package bssm.plantshuman.peopleandgreen.recommendation.adapter.in.web.dto.response;

import bssm.plantshuman.peopleandgreen.recommendation.domain.model.PlantRecommendation;

import java.util.List;

public record PlantRecommendationResponse(
        String plantId,
        String plantName,
        String plantEnglishName,
        int score,
        List<String> reasons,
        List<String> cautions,
        String representativeEnvironment,
        List<String> secondaryEnvironmentTags,
        String airPurificationLevel,
        String petSafetyLevel,
        String difficultyLevel,
        String sizeCategory,
        List<String> recommendedPlacements,
        String description
) {

    public static PlantRecommendationResponse from(PlantRecommendation recommendation) {
        return new PlantRecommendationResponse(
                recommendation.plantId(),
                recommendation.plantName(),
                recommendation.plantEnglishName(),
                recommendation.score(),
                recommendation.reasons(),
                recommendation.cautions(),
                recommendation.representativeEnvironment().name(),
                recommendation.secondaryEnvironmentTags().stream().map(Enum::name).toList(),
                recommendation.airPurificationLevel().name(),
                recommendation.petSafetyLevel().name(),
                recommendation.difficultyLevel().name(),
                recommendation.sizeCategory().name(),
                recommendation.recommendedPlacements().stream().map(Enum::name).toList(),
                recommendation.description()
        );
    }
}

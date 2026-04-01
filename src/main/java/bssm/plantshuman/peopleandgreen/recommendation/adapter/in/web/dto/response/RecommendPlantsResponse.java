package bssm.plantshuman.peopleandgreen.recommendation.adapter.in.web.dto.response;

import bssm.plantshuman.peopleandgreen.recommendation.domain.model.RecommendPlantsResult;

import java.util.List;

public record RecommendPlantsResponse(
        String representativeEnvironment,
        List<String> secondaryEnvironmentTags,
        List<PlantRecommendationResponse> plants
) {

    public static RecommendPlantsResponse from(RecommendPlantsResult result) {
        return new RecommendPlantsResponse(
                result.representativeEnvironment().name(),
                result.secondaryEnvironmentTags().stream().map(Enum::name).toList(),
                result.plants().stream().map(PlantRecommendationResponse::from).toList()
        );
    }
}

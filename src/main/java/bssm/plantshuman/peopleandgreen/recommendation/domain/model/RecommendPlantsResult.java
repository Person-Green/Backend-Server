package bssm.plantshuman.peopleandgreen.recommendation.domain.model;

import java.util.List;

public record RecommendPlantsResult(
        EnvironmentType representativeEnvironment,
        List<EnvironmentType> secondaryEnvironmentTags,
        List<PlantRecommendation> plants
) {
}

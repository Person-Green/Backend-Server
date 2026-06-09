package bssm.plantshuman.peopleandgreen.recommendation.domain.model;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public record RecommendPlantsResult(
        EnvironmentType representativeEnvironment,
        List<EnvironmentType> secondaryEnvironmentTags,
        List<PlantRecommendation> plants
) {
    public Set<String> plantIds() {
        return plants.stream()
                .map(PlantRecommendation::plantId)
                .collect(Collectors.toSet());
    }

    public RecommendPlantsResult withFavoriteMetadata(RecommendationFavoriteMetadata favoriteMetadata) {
        return new RecommendPlantsResult(
                representativeEnvironment,
                secondaryEnvironmentTags,
                plants.stream()
                        .map(plant -> plant.withFavoriteMetadata(
                                favoriteMetadata.isFavorite(plant.plantId()),
                                favoriteMetadata.favoriteCount(plant.plantId())
                        ))
                        .toList()
        );
    }

    public RecommendPlantsResult withoutFavoriteMetadata() {
        return new RecommendPlantsResult(
                representativeEnvironment,
                secondaryEnvironmentTags,
                plants.stream()
                        .map(plant -> plant.withFavoriteMetadata(false, 0L))
                        .toList()
        );
    }
}

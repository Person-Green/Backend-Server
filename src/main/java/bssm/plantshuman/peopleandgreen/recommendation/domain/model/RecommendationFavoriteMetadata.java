package bssm.plantshuman.peopleandgreen.recommendation.domain.model;

import java.util.Map;
import java.util.Set;

public record RecommendationFavoriteMetadata(
        Set<String> favoritePlantIds,
        Map<String, Long> favoriteCountByPlantId
) {
    public RecommendationFavoriteMetadata {
        favoritePlantIds = Set.copyOf(favoritePlantIds);
        favoriteCountByPlantId = Map.copyOf(favoriteCountByPlantId);
    }

    public static RecommendationFavoriteMetadata empty() {
        return new RecommendationFavoriteMetadata(Set.of(), Map.of());
    }

    public boolean isFavorite(String plantId) {
        return favoritePlantIds.contains(plantId);
    }

    public long favoriteCount(String plantId) {
        return favoriteCountByPlantId.getOrDefault(plantId, 0L);
    }
}

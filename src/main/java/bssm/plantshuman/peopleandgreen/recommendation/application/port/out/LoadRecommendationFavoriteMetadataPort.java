package bssm.plantshuman.peopleandgreen.recommendation.application.port.out;

import bssm.plantshuman.peopleandgreen.recommendation.domain.model.RecommendationFavoriteMetadata;

import java.util.Set;

public interface LoadRecommendationFavoriteMetadataPort {

    RecommendationFavoriteMetadata load(Long userId, Set<String> plantIds);
}

package bssm.plantshuman.peopleandgreen.recommendation.adapter.out.persistence;

import bssm.plantshuman.peopleandgreen.catalog.adapter.out.persistence.repository.FavoritePlantRepository;
import bssm.plantshuman.peopleandgreen.recommendation.application.port.out.LoadRecommendationFavoriteMetadataPort;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.RecommendationFavoriteMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RecommendationFavoriteMetadataPersistenceAdapter implements LoadRecommendationFavoriteMetadataPort {

    private final FavoritePlantRepository favoritePlantRepository;

    @Override
    public RecommendationFavoriteMetadata load(Long userId, Set<String> plantIds) {
        if (plantIds.isEmpty()) {
            return RecommendationFavoriteMetadata.empty();
        }

        Set<String> favoritePlantIds = favoritePlantRepository.findFavoritePlantIds(userId, plantIds);
        Map<String, Long> favoriteCountByPlantId = favoritePlantRepository.countGroupedByPlantId(plantIds)
                .stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> (Long) row[1]
                ));

        return new RecommendationFavoriteMetadata(favoritePlantIds, favoriteCountByPlantId);
    }
}

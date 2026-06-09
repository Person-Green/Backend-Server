package bssm.plantshuman.peopleandgreen.recommendation.application.service;

import bssm.plantshuman.peopleandgreen.recommendation.application.port.in.GetRecommendationHistoryUseCase;
import bssm.plantshuman.peopleandgreen.recommendation.application.port.out.LoadRecommendationFavoriteMetadataPort;
import bssm.plantshuman.peopleandgreen.recommendation.application.port.out.RecommendationHistoryQueryPort;
import bssm.plantshuman.peopleandgreen.recommendation.domain.exception.RecommendationHistoryNotFoundException;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.RecommendPlantsResult;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.RecommendationFavoriteMetadata;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.RecommendationHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetRecommendationHistoryService implements GetRecommendationHistoryUseCase {

    private final RecommendationHistoryQueryPort recommendationHistoryQueryPort;
    private final LoadRecommendationFavoriteMetadataPort loadRecommendationFavoriteMetadataPort;

    @Override
    public RecommendationHistory getHistory(Long userId, Long historyId) {
        RecommendationHistory history = recommendationHistoryQueryPort.getHistory(userId, historyId)
                .orElseThrow(RecommendationHistoryNotFoundException::new);
        RecommendPlantsResult result = history.resultSnapshot();
        RecommendationFavoriteMetadata favoriteMetadata = loadRecommendationFavoriteMetadataPort.load(userId, result.plantIds());

        return new RecommendationHistory(
                history.id(),
                history.userId(),
                history.title(),
                history.plantSummaryText(),
                history.requestSnapshot(),
                result.withFavoriteMetadata(favoriteMetadata),
                history.createdAt()
        );
    }
}

package bssm.plantshuman.peopleandgreen.recommendation.application.service;

import bssm.plantshuman.peopleandgreen.recommendation.application.port.in.GetRecommendationHistoriesUseCase;
import bssm.plantshuman.peopleandgreen.recommendation.application.port.out.RecommendationHistoryQueryPort;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.RecommendationHistoryCursorPage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetRecommendationHistoriesService implements GetRecommendationHistoriesUseCase {

    private final RecommendationHistoryQueryPort recommendationHistoryQueryPort;

    @Override
    public RecommendationHistoryCursorPage getHistories(Long userId, String cursor, int size) {
        return recommendationHistoryQueryPort.getHistories(userId, cursor, size);
    }
}

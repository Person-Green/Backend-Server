package bssm.plantshuman.peopleandgreen.recommendation.application.service;

import bssm.plantshuman.peopleandgreen.recommendation.application.port.in.GetRecommendationHistoryUseCase;
import bssm.plantshuman.peopleandgreen.recommendation.application.port.out.RecommendationHistoryQueryPort;
import bssm.plantshuman.peopleandgreen.recommendation.domain.exception.RecommendationHistoryNotFoundException;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.RecommendationHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetRecommendationHistoryService implements GetRecommendationHistoryUseCase {

    private final RecommendationHistoryQueryPort recommendationHistoryQueryPort;

    @Override
    public RecommendationHistory getHistory(Long userId, Long historyId) {
        return recommendationHistoryQueryPort.getHistory(userId, historyId)
                .orElseThrow(RecommendationHistoryNotFoundException::new);
    }
}

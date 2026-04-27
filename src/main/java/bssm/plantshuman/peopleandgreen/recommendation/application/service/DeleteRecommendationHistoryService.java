package bssm.plantshuman.peopleandgreen.recommendation.application.service;

import bssm.plantshuman.peopleandgreen.recommendation.application.port.in.DeleteRecommendationHistoryUseCase;
import bssm.plantshuman.peopleandgreen.recommendation.application.port.out.RecommendationHistoryCommandPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteRecommendationHistoryService implements DeleteRecommendationHistoryUseCase {

    private final RecommendationHistoryCommandPort recommendationHistoryCommandPort;

    @Override
    public void delete(Long userId, Long historyId) {
        recommendationHistoryCommandPort.delete(userId, historyId);
    }
}

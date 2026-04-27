package bssm.plantshuman.peopleandgreen.recommendation.application.port.in;

import bssm.plantshuman.peopleandgreen.recommendation.domain.model.RecommendationHistory;

public interface GetRecommendationHistoryUseCase {

    RecommendationHistory getHistory(Long userId, Long historyId);
}

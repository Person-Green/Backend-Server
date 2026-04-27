package bssm.plantshuman.peopleandgreen.recommendation.application.port.in;

import bssm.plantshuman.peopleandgreen.recommendation.domain.model.RecommendationHistoryCursorPage;

public interface GetRecommendationHistoriesUseCase {

    RecommendationHistoryCursorPage getHistories(Long userId, Long cursor, int size);
}

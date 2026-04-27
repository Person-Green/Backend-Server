package bssm.plantshuman.peopleandgreen.recommendation.application.port.out;

import bssm.plantshuman.peopleandgreen.recommendation.domain.model.RecommendationHistory;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.RecommendationHistoryCursorPage;

import java.util.Optional;

public interface RecommendationHistoryQueryPort {

    RecommendationHistoryCursorPage getHistories(Long userId, String cursor, int size);

    Optional<RecommendationHistory> getHistory(Long userId, Long historyId);
}

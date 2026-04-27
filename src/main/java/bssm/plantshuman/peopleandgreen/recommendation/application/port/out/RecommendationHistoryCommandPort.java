package bssm.plantshuman.peopleandgreen.recommendation.application.port.out;

import bssm.plantshuman.peopleandgreen.recommendation.domain.model.RecommendationHistoryDraft;

public interface RecommendationHistoryCommandPort {

    Long save(RecommendationHistoryDraft draft);

    void delete(Long userId, Long historyId);
}

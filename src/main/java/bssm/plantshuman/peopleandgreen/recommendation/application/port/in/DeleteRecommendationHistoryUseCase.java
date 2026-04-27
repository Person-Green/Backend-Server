package bssm.plantshuman.peopleandgreen.recommendation.application.port.in;

public interface DeleteRecommendationHistoryUseCase {

    void delete(Long userId, Long historyId);
}

package bssm.plantshuman.peopleandgreen.recommendation.application.port.in;

import bssm.plantshuman.peopleandgreen.recommendation.domain.model.RecommendPlantsCommand;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.RecommendPlantsExecutionResult;

public interface RecommendPlantsWithHistoryUseCase {

    RecommendPlantsExecutionResult recommend(Long userId, RecommendPlantsCommand command);
}

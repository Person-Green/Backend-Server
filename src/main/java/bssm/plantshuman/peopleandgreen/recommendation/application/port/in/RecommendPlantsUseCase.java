package bssm.plantshuman.peopleandgreen.recommendation.application.port.in;

import bssm.plantshuman.peopleandgreen.recommendation.domain.model.RecommendPlantsCommand;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.RecommendPlantsResult;

public interface RecommendPlantsUseCase {

    RecommendPlantsResult recommend(RecommendPlantsCommand command);
}

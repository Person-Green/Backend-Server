package bssm.plantshuman.peopleandgreen.recommendation.domain.exception;

import bssm.plantshuman.peopleandgreen.recommendation.domain.exception.error.RecommendationHistoryErrorProperty;
import bssm.plantshuman.peopleandgreen.shared.error.PeopleAndGreenException;

public class RecommendationHistoryNotFoundException extends PeopleAndGreenException {

    public RecommendationHistoryNotFoundException() {
        super(RecommendationHistoryErrorProperty.RECOMMENDATION_HISTORY_NOT_FOUND);
    }
}

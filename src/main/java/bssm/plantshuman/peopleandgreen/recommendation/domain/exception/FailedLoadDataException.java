package bssm.plantshuman.peopleandgreen.recommendation.domain.exception;

import bssm.plantshuman.peopleandgreen.recommendation.domain.exception.error.RecommendationErrorProperty;
import bssm.plantshuman.peopleandgreen.shared.error.PeopleAndGreenException;

public class FailedLoadDataException extends PeopleAndGreenException {

    public FailedLoadDataException() {
        super(RecommendationErrorProperty.FAILED_LOAD_DATA);
    }
}

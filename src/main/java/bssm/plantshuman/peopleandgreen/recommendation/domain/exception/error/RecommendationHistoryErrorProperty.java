package bssm.plantshuman.peopleandgreen.recommendation.domain.exception.error;

import bssm.plantshuman.peopleandgreen.shared.error.ErrorProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum RecommendationHistoryErrorProperty implements ErrorProperty {

    RECOMMENDATION_HISTORY_NOT_FOUND(HttpStatus.NOT_FOUND, "Recommendation history not found");

    private final HttpStatus status;
    private final String message;
}

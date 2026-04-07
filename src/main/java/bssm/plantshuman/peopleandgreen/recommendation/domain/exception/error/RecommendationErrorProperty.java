package bssm.plantshuman.peopleandgreen.recommendation.domain.exception.error;

import bssm.plantshuman.peopleandgreen.shared.error.ErrorProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum RecommendationErrorProperty implements ErrorProperty {
    FAILED_LOAD_DATA(HttpStatus.INTERNAL_SERVER_ERROR, "데이터를 불러오느데 실패했습니다.");

    private final HttpStatus status;
    private final String message;
}

package bssm.plantshuman.peopleandgreen.recommendation.adapter.in.web;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RecommendationExceptionHandlerTest {

    @Test
    void mapsIllegalArgumentExceptionToBadRequest() {
        RecommendationExceptionHandler handler = new RecommendationExceptionHandler();

        ResponseEntity<RecommendationExceptionHandler.ErrorResponse> response =
                handler.handleIllegalArgument(new IllegalArgumentException("sunlight is required"));

        assertEquals(400, response.getStatusCode().value());
        assertEquals("sunlight is required", response.getBody().message());
    }
}

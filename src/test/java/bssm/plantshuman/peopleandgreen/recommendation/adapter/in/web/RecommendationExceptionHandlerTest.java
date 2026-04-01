package bssm.plantshuman.peopleandgreen.recommendation.adapter.in.web;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.core.MethodParameter;

import java.lang.reflect.Method;

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

    @Test
    void mapsMethodArgumentNotValidExceptionToBadRequest() throws NoSuchMethodException {
        RecommendationExceptionHandler handler = new RecommendationExceptionHandler();
        Method method = PlantRecommendationController.class
                .getDeclaredMethod("recommend", bssm.plantshuman.peopleandgreen.recommendation.adapter.in.web.dto.request.RecommendPlantsRequest.class);
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "request");
        bindingResult.addError(new FieldError("request", "hasPet", "hasPet is required"));

        ResponseEntity<RecommendationExceptionHandler.ErrorResponse> response =
                handler.handleMethodArgumentNotValid(new MethodArgumentNotValidException(new MethodParameter(method, 0), bindingResult));

        assertEquals(400, response.getStatusCode().value());
        assertEquals("hasPet is required", response.getBody().message());
    }
}

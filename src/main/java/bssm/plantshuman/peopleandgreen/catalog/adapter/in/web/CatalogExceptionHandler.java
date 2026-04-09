package bssm.plantshuman.peopleandgreen.catalog.adapter.in.web;

import bssm.plantshuman.peopleandgreen.shared.error.GlobalErrorProperty;
import bssm.plantshuman.peopleandgreen.shared.error.PeopleAndGreenException;
import bssm.plantshuman.peopleandgreen.shared.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

@RestControllerAdvice(assignableTypes = PlantCatalogController.class)
public class CatalogExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException exception) {
        return ResponseEntity.badRequest().body(new ErrorResponse(GlobalErrorProperty.BAD_REQUEST, exception.getMessage()));
    }

    @ExceptionHandler(PeopleAndGreenException.class)
    public ResponseEntity<ErrorResponse> handlePeopleAndGreenException(PeopleAndGreenException exception) {
        return ResponseEntity.status(exception.getErrorProperty().getStatus())
                .body(new ErrorResponse(exception.getErrorProperty(), exception.getMessage()));
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            HandlerMethodValidationException.class
    })
    public ResponseEntity<ErrorResponse> handleValidation(Exception exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(GlobalErrorProperty.BAD_REQUEST));
    }
}

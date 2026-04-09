package bssm.plantshuman.peopleandgreen.catalog.adapter.in.web;

import bssm.plantshuman.peopleandgreen.catalog.domain.exception.PlantNotFoundException;
import bssm.plantshuman.peopleandgreen.shared.error.GlobalErrorProperty;
import bssm.plantshuman.peopleandgreen.shared.response.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CatalogExceptionHandlerTest {

    @Test
    void returnsSharedBadRequestResponseForIllegalArgument() {
        CatalogExceptionHandler handler = new CatalogExceptionHandler();

        ResponseEntity<ErrorResponse> response = handler.handleIllegalArgument(
                new IllegalArgumentException("Catalog size must be between 1 and 50")
        );

        assertEquals(400, response.getStatusCode().value());
        assertEquals(GlobalErrorProperty.BAD_REQUEST.name(), response.getBody().getCode());
        assertEquals("Catalog size must be between 1 and 50", response.getBody().getMessage());
    }

    @Test
    void returnsSharedNotFoundResponseForMissingPlant() {
        CatalogExceptionHandler handler = new CatalogExceptionHandler();

        ResponseEntity<ErrorResponse> response = handler.handlePeopleAndGreenException(new PlantNotFoundException());

        assertEquals(404, response.getStatusCode().value());
        assertEquals(GlobalErrorProperty.NOT_FOUND.name(), response.getBody().getCode());
        assertEquals(GlobalErrorProperty.NOT_FOUND.getMessage(), response.getBody().getMessage());
    }
}

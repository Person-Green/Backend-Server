package bssm.plantshuman.peopleandgreen.catalog.adapter.in.web;

import bssm.plantshuman.peopleandgreen.auth.adapter.out.security.AuthenticatedUser;
import bssm.plantshuman.peopleandgreen.catalog.application.port.in.AddFavoritePlantUseCase;
import bssm.plantshuman.peopleandgreen.catalog.application.port.in.GetPlantCatalogUseCase;
import bssm.plantshuman.peopleandgreen.catalog.application.port.in.RemoveFavoritePlantUseCase;
import bssm.plantshuman.peopleandgreen.catalog.domain.model.PlantCatalogCursorPage;
import bssm.plantshuman.peopleandgreen.catalog.domain.model.PlantCatalogView;
import bssm.plantshuman.peopleandgreen.domain.plant.AirPurification;
import bssm.plantshuman.peopleandgreen.domain.plant.ManageDifficulty;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlantCatalogControllerTest {

    @Test
    void returnsCatalogPageForAuthenticatedUser() {
        PlantCatalogController controller = new PlantCatalogController(
                (userId, cursor, size) -> new PlantCatalogCursorPage(
                        List.of(new PlantCatalogView("PLT-001", "스투키", "Stucky", "중형", AirPurification.HIGH, ManageDifficulty.EASY, true)),
                        "PLT-001",
                        false
                ),
                (userId, plantId) -> { },
                (userId, plantId) -> { }
        );

        ResponseEntity<PlantCatalogPageResponse> response = controller.getPlants(new AuthenticatedUser(1L), null, 20);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("PLT-001", response.getBody().plants().getFirst().plantId());
        assertEquals(true, response.getBody().plants().getFirst().isFavorite());
    }

    @Test
    void addsFavoriteForAuthenticatedUser() {
        RecordingFavoriteUseCase addFavoriteUseCase = new RecordingFavoriteUseCase();
        PlantCatalogController controller = new PlantCatalogController(
                (userId, cursor, size) -> new PlantCatalogCursorPage(List.of(), null, false),
                addFavoriteUseCase,
                (userId, plantId) -> { }
        );

        ResponseEntity<Void> response = controller.addFavorite(new AuthenticatedUser(1L), "PLT-001");

        assertEquals(204, response.getStatusCode().value());
        assertEquals(1L, addFavoriteUseCase.userId);
        assertEquals("PLT-001", addFavoriteUseCase.plantId);
    }

    private static final class RecordingFavoriteUseCase implements AddFavoritePlantUseCase {

        private Long userId;
        private String plantId;

        @Override
        public void add(Long userId, String plantId) {
            this.userId = userId;
            this.plantId = plantId;
        }
    }
}
